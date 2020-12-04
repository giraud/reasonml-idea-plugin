package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.ORProcessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BscProcess {

  private static final Log LOG = Log.create("process.bsc");

  private static final Duration TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

  private final Project m_project;

  public static BscProcess getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, BscProcess.class);
  }

  public BscProcess(Project project) {
    m_project = project;
  }

  @Nullable
  public Integer run(
      @NotNull VirtualFile sourceFile,
      @NotNull List<String> arguments,
      @NotNull ProcessListener processListener)
      throws ORProcessException {
    Optional<VirtualFile> bscPath = BsPlatform.findBscExecutable(m_project, sourceFile);
    if (!bscPath.isPresent()) {
      LOG.error("Unable to find bsc.exe.");
      return null;
    }

    List<String> command = new ArrayList<>();
    command.add(bscPath.get().getPath());
    command.addAll(arguments);
    GeneralCommandLine bscCli =
        new GeneralCommandLine(command)
            .withWorkDirectory(sourceFile.getParent().getPath())
            // disable coloring
            // https://rescript-lang.org/docs/manual/latest/build-configuration#error-output-coloring-ninja_ansi_forced
            .withEnvironment("NINJA_ANSI_FORCED", "0");

    OSProcessHandler bscProcessHandler;
    try {
      bscProcessHandler = new KillableProcessHandler(bscCli);
    } catch (ExecutionException e) {
      throw new ORProcessException(e.getMessage());
    }

    bscProcessHandler.addProcessListener(processListener);
    bscProcessHandler.startNotify();
    bscProcessHandler.waitFor(TIMEOUT.toMillis());
    return bscProcessHandler.getExitCode();
  }
}
