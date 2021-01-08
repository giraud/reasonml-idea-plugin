package com.reason.bs;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import org.jetbrains.annotations.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

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
  public Integer run(@NotNull VirtualFile sourceFile, @NotNull VirtualFile workDir, @NotNull List<String> arguments, @NotNull ProcessListener processListener) throws ORProcessException {
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
            .withWorkDirectory(workDir.getPath())
            // disable coloring
            // https://rescript-lang.org/docs/manual/latest/build-configuration#error-output-coloring-ninja_ansi_forced
            .withEnvironment("NINJA_ANSI_FORCED", "0");
    if (LOG.isTraceEnabled()) {
      LOG.trace("bsc " + Joiner.join(" ", arguments));
      LOG.trace("  work dir", sourceFile.getParent());
    }

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
