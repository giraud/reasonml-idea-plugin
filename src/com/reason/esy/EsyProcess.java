package com.reason.esy;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.*;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.intellij.notification.NotificationType.ERROR;
import static com.reason.esy.EsyConstants.ESY_EXECUTABLE_NAME;
import static com.reason.esy.EsyProcessException.esyNotFoundException;

public class EsyProcess implements CompilerProcess {

  private static final Log LOG = Log.create("process.esy");

  @Nls
  private static final Runnable SHOW_ESY_NOT_FOUND_NOTIFICATION =
          () -> Notifications.Bus.notify(new ORNotification("Esy Missing",
                  "Unable to find esy executable in system PATH.", ERROR));

  @Nls
  private static final Runnable SHOW_ESY_PROJECT_NOT_FOUND_NOTIFICATION =
          () -> Notifications.Bus.notify(new ORNotification("Esy Project Not Found",
                  "Unable to find esy project. Have you run esy yet?", ERROR));

  @Nls
  private static final Consumer<Exception> SHOW_EXEC_EXCEPTION_NOTIFICATION =
          (e) -> Notifications.Bus.notify(new ORNotification("Esy Exception",
                  "Failed to execute esy command.\n" + e.getMessage(), ERROR));

  public static class Command {
    public static final String ESY = "";
    public static final String INSTALL = "install";
    public static final String BUILD = "build";
    public static final String SHELL = "shell";
  }

  private final Path workingDir;

  private final Path esyExecutable;

  private final boolean redirectErrors;

  private final AtomicBoolean isStarted;

  @Nullable
  private KillableColoredProcessHandler processHandler;

  public static EsyProcess getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, EsyProcess.class);
  }

  private EsyProcess(@NotNull Project project) {
    FileSystem fileSystem = FileSystems.getDefault();
    Optional<VirtualFile> esyContentRoot = findEsyContentRoot(project);
    String esyRootAsString = esyContentRoot.map(VirtualFile::getPath).orElse("");
    this.workingDir = fileSystem.getPath(esyRootAsString);
    this.esyExecutable = findEsyExecutableInPath();
    this.redirectErrors = true;
    this.isStarted = new AtomicBoolean(false);
  }

  public boolean isStarted() {
    return isStarted.get();
  }

  @Override
  public boolean start() {
    if (isStarted()) {
      LOG.warn("Esy process already started.");
    }
    return isStarted.compareAndSet(false, true);
  }

  @Override
  public void terminate() {
    if (!isStarted()) {
      LOG.warn("Esy process already terminated.");
      return;
    }
    isStarted.set(false);
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

  @Nullable
  @Override
  public ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
    killIt();
    GeneralCommandLine cli = newCommandLine((CliType.Esy) cliType);
    try {
      processHandler = new KillableColoredProcessHandler(cli);
    } catch (ExecutionException e) {
      SHOW_EXEC_EXCEPTION_NOTIFICATION.accept(e);
      return null;
    }

    if (onProcessTerminated != null) {
      processHandler.addProcessListener(processTerminatedListener.apply(onProcessTerminated));
    }

    return processHandler;
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

  private GeneralCommandLine newCommandLine(CliType.Esy cliType) {
    GeneralCommandLine commandLine;
    commandLine = new GeneralCommandLine(esyExecutable.toString());
    commandLine.setWorkDirectory(workingDir.toFile());
    commandLine.setRedirectErrorStream(redirectErrors);
    commandLine.addParameter(getCommand(cliType)); // 'esy + command' must be a single parameter
    return commandLine;
  }

  private static String getCommand(CliType.Esy cliType) {
    switch (cliType) {
      case INSTALL:
        return Command.INSTALL;
      case BUILD:
        return Command.BUILD;
      case SHELL:
        return Command.SHELL;
      default:
        return Command.ESY;
    }
  }

  private static Function<Compiler.ProcessTerminated, ProcessListener> processTerminatedListener =
          (onProcessTerminated) -> new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
              onProcessTerminated.run();
            }
          };

  private static Path findEsyExecutableInPath() {
    return Platform.findExecutableInPath(ESY_EXECUTABLE_NAME, System.getenv("PATH"))
            .orElseThrow(() -> {
              SHOW_ESY_NOT_FOUND_NOTIFICATION.run();
              return esyNotFoundException();
            });
  }

  private static Optional<VirtualFile> findEsyContentRoot(@NotNull Project project) {
    Optional<VirtualFile> esyContentRoot = ORProjectManager.findFirstEsyContentRoot(project);
    if (!esyContentRoot.isPresent()) {
      SHOW_ESY_PROJECT_NOT_FOUND_NOTIFICATION.run();
      return Optional.empty();
    }
    return esyContentRoot;
  }
}