package com.reason.ide.repl;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReplGenericState implements RunProfileState {
  private final ExecutionEnvironment m_environment;

  ReplGenericState(ExecutionEnvironment environment) {
    m_environment = environment;
  }

  @Nullable
  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner)
      throws ExecutionException {
    ProcessHandler processHandler = startProcess();
    if (processHandler == null) {
      return null;
    }

    PromptConsoleView consoleView = new PromptConsoleView(m_environment.getProject(), true, true);
    consoleView.attachToProcess(processHandler);

    return new DefaultExecutionResult(consoleView, processHandler);
  }

  @Nullable
  private ProcessHandler startProcess() throws ExecutionException {
    ReplRunConfiguration runProfile = (ReplRunConfiguration) m_environment.getRunProfile();
    Sdk runSdk = runProfile.getSdk();
    if (runSdk == null) {
      return null;
    }

    VirtualFile homeDirectory = runSdk.getHomeDirectory();
    if (homeDirectory == null) {
      return null;
    }

    GeneralCommandLine cmd = null;

    if (SystemInfo.isWindows) {
      VirtualFile ocamlBinFile = homeDirectory.findFileByRelativePath("bin/ocaml.exe");
      if (ocamlBinFile != null) {
        String ocamlBinPath = ocamlBinFile.getPath();
        if (runProfile.getCygwinSelected()) {
          cmd = new GeneralCommandLine(runProfile.getCygwinPath(), "--login", "-c", ocamlBinPath);
        } else {
          cmd = new GeneralCommandLine(ocamlBinPath);
        }
      }
    } else {
      VirtualFile ocamlBinFile = homeDirectory.findFileByRelativePath("bin/ocaml");
      if (ocamlBinFile != null) {
        String ocamlBinPath = ocamlBinFile.getPath();
        cmd = new GeneralCommandLine(ocamlBinPath);
      }
    }

    if (cmd != null) {
      OSProcessHandler handler = new OSProcessHandler(cmd);
      ProcessTerminatedListener.attach(handler, m_environment.getProject());
      return handler;
    }

    return null;
  }
}
