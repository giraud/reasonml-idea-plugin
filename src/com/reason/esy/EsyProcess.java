package com.reason.esy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.*;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.CompilerProcess;
import com.reason.Log;
import com.reason.Platform;
import com.reason.dune.DuneOutputListener;
import com.reason.ide.ORNotification;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.intellij.notification.NotificationType.ERROR;
import static com.reason.esy.EsyProcessException.esyNotFoundException;

public class EsyProcess implements CompilerProcess {

  public static final String DUNE_EXECUTABLE_NAME = "dune";
  public static final String ESY_EXECUTABLE_NAME = "esy";
  public static final String WINDOWS_EXECUTABLE_SUFFIX =  ".exe";

  private static final Runnable SHOW_DUNE_NOT_FOUND_NOTIFICATION =
      () -> Notifications.Bus.notify(new ORNotification("Dune Missing", "Unable to find dune executable in esy PATH.", ERROR));

  private static final Runnable SHOW_ESY_NOT_FOUND_NOTIFICATION =
      () -> Notifications.Bus.notify(new ORNotification("Esy Missing", "Unable to find esy executable in system PATH.", ERROR));

  private static final Consumer<Exception> SHOW_EXEC_EXCEPTION_NOTIFICATION =
      (e) -> Notifications.Bus.notify(new ORNotification("Esy Exception", "Failed to execute esy command.\n" + e.getMessage(), ERROR));

  private static final Consumer<Exception> SHOW_DUNE_EXCEPTION_NOTIFICATION =
      (e) -> Notifications.Bus.notify(new ORNotification("Dune Exception", "Failed to execute dune command.\n" + e.getMessage(), ERROR));

  private static final Log LOG = Log.create("esy");

  public static class Command {
    public static final String ENV = "env";
    public static final String PRINT_ENV = "print-env";
  }

  public static class EnvironmentVariable {
    public static final String CAML_LD_LIBRARY_PATH = "CAML_LD_LIBRARY_PATH";
    public static final String DUNE_STORE_ORIG_SOURCE_DIR = "DUNE_STORE_ORIG_SOURCE_DIR";
    public static final String ESY__ROOT_PACKAGE_CONFIG_PATH = "ESY__ROOT_PACKAGE_CONFIG_PATH";
    public static final String DUNE_BUILD_DIR = "DUNE_BUILD_DIR";
    public static final String MAN_PATH = "MAN_PATH";
    public static final String OCAMLFIND_DESTDIR = "OCAMLFIND_DESTDIR";
    public static final String OCAMLFIND_LDCONF = "OCAMLFIND_LDCONF";
    public static final String OCAMLLIB = "OCAMLLIB";
    public static final String OCAMLPATH = "OCAMLPATH";
    public static final String PATH = "PATH";
    public static final Set<String> ALL = ImmutableSet.of(
        CAML_LD_LIBRARY_PATH,
        DUNE_STORE_ORIG_SOURCE_DIR,
        ESY__ROOT_PACKAGE_CONFIG_PATH,
        CAML_LD_LIBRARY_PATH,
        DUNE_BUILD_DIR,
        MAN_PATH,
        OCAMLFIND_DESTDIR,
        OCAMLFIND_LDCONF,
        OCAMLLIB,
        OCAMLPATH,
        PATH
    );
  }

  private final Project project;

  private final ProcessListener outputListener;

  private final Path workingDir;

  private final Path esyExecutable;

  private final boolean redirectErrors;

  private final AtomicBoolean started;

  @Nullable
  private KillableColoredProcessHandler processHandler;

  public static EsyProcess getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, EsyProcess.class);
  }

  private EsyProcess(@NotNull Project project) {
    FileSystem fileSystem = FileSystems.getDefault();
    VirtualFile esyContentRoot = Platform.findOREsyContentRoot(project);
    Path esyExecutable = findExecutableInPath(ESY_EXECUTABLE_NAME, System.getenv("PATH"))
        .orElseThrow(() -> {
          SHOW_ESY_NOT_FOUND_NOTIFICATION.run();
          return esyNotFoundException();
        });
    this.project = project;
    this.outputListener = new DuneOutputListener(project, this);
    this.workingDir = fileSystem.getPath(esyContentRoot.getPath());
    this.esyExecutable = esyExecutable;
    this.redirectErrors = true;
    this.started = new AtomicBoolean(false);
  }

  public boolean isStarted() {
    return started.get();
  }

  @Override
  public boolean start() {
    if (isStarted()) {
      LOG.warn("Esy process already started.");
    }
    return started.compareAndSet(false, true);
  }

  @Override
  public void terminate() {
    if (!isStarted()) {
      LOG.warn("Esy process already terminated.");
      return;
    }
    started.set(false);
  }

  @Override
  public void startNotify() {
    if (processHandler != null && !processHandler.isStartNotified()) {
      try {
        processHandler.startNotify();
      } catch (Exception e) {
        LOG.error("Exception when calling 'startNotify'.", e);
      }
    }
  }

  @Override
  public ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
    killIt();

    Optional<GeneralCommandLine> commandLineOptional = getDuneCommandLine(cliType);
    if (!commandLineOptional.isPresent()) {
      SHOW_DUNE_NOT_FOUND_NOTIFICATION.run();
      return null;
    }

    GeneralCommandLine commandLine = commandLineOptional.get();
    try {
        processHandler = new KillableColoredProcessHandler(commandLine);
        processHandler.addProcessListener(outputListener);
        if (onProcessTerminated != null) {
          processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
              onProcessTerminated.run();
            }
          });
        }
      return processHandler;
    } catch (ExecutionException e) {
      SHOW_DUNE_EXCEPTION_NOTIFICATION.accept(e);
      LOG.error("Unable to recreate esy process.", e);
    }
    return null;
  }

  public Map<String, String> getEsyEnvironment() {
    ImmutableMap.Builder<String, String> envBuilder = ImmutableMap.builder();
    Consumer<String> handleLine = line -> {
      String[] keyValue = line.split("=");
      if (EnvironmentVariable.ALL.contains(keyValue[0])) {
        envBuilder.put(keyValue[0], keyValue[1]);
      }
    };
    exec(Command.ENV, handleLine);
    return envBuilder.build();
  }

  public void exec(String command, Consumer<String> handleLine) {
    GeneralCommandLine commandLine = newEsyCommandLine(command);
    try {
      Process process = commandLine.createProcess();
      InputStreamReader processInputStream = new InputStreamReader(process.getInputStream());
      BufferedReader reader = new BufferedReader(processInputStream);
      String line;
      while ((line = reader.readLine()) != null) {
        handleLine.accept(line);
      }
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        LOG.error("Esy command exiting with non-zero code: " + exitCode);
      }
    } catch (IOException | InterruptedException | ExecutionException e) {
      SHOW_EXEC_EXCEPTION_NOTIFICATION.accept(e);
      LOG.error("Exception while executing esy command.", e);
    }
  }

  private void killIt() {
    if (processHandler == null
        || processHandler.isProcessTerminating()
        || processHandler.isProcessTerminated()) {
      return;
    }
    processHandler.killProcess();
    processHandler = null;
  }

  private GeneralCommandLine newEsyCommandLine(String command) {
    GeneralCommandLine commandLine;
    if (SystemInfo.isWindows) {
      commandLine = new GeneralCommandLine("cmd.exe");
      commandLine.addParameter("/c");
    } else {
      commandLine = new GeneralCommandLine("sh");
      commandLine.addParameter("-c");
    }
    commandLine.setWorkDirectory(workingDir.toFile());
    commandLine.setRedirectErrorStream(redirectErrors);
    commandLine.addParameter(esyExecutable + " " + command); // 'esy + command' must be a single parameter
    return commandLine;
  }

  private Optional<GeneralCommandLine> getDuneCommandLine(CliType cliType) {
    Optional<Path> duneExecutable = findDuneExecutableWithinEsy();
    if (!duneExecutable.isPresent()) {
      return Optional.empty();
    }

    VirtualFile esyContentRoot = Platform.findOREsyContentRoot(project);

    String duneCommand = cliType == CliType.clean ? "clean" : "build"; // @TODO support all actions

    GeneralCommandLine cli = new GeneralCommandLine(duneExecutable.get().toString(), duneCommand);
    cli.withEnvironment(getEsyEnvironment());
    cli.setWorkDirectory(esyContentRoot.getPath());
    cli.setRedirectErrorStream(true);
    return Optional.of(cli);
  }

  private Optional<Path> findDuneExecutableWithinEsy() {
    Map<String, String> esyEnv = getEsyEnvironment();
    String paths = esyEnv.get(EnvironmentVariable.PATH);
    if (paths == null) {
      return Optional.empty();
    }
    return findExecutableInPath(DUNE_EXECUTABLE_NAME, paths);
  }

  private static Optional<Path> findExecutableInPath(String filename, String shellPath) {
    if (SystemInfo.isWindows) {
      filename += WINDOWS_EXECUTABLE_SUFFIX;
    }
    File exeFile = PathEnvironmentVariableUtil.findInPath(filename, shellPath, null);
    return exeFile == null ? Optional.empty() : Optional.of(exeFile.toPath());
  }
}
