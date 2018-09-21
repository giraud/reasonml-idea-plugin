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
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();

        PromptConsoleView consoleView = new PromptConsoleView(m_environment.getProject(), true, true);
        consoleView.attachToProcess(processHandler);

        return new DefaultExecutionResult(consoleView, processHandler);
    }

    private ProcessHandler startProcess() throws ExecutionException {
        ReplRunConfiguration runProfile = (ReplRunConfiguration) m_environment.getRunProfile();
        Sdk runSdk = runProfile.getSdk();
        VirtualFile homeDirectory = runSdk.getHomeDirectory();

        GeneralCommandLine cmd;

        if (SystemInfo.isWindows) {
            VirtualFile ocamlBinFile = homeDirectory.findFileByRelativePath("bin/ocaml.exe");
            String ocamlBinPath = ocamlBinFile.getPath();

            cmd = new GeneralCommandLine("C:/OCaml64/bin/bash.exe", "--login", "-c", ocamlBinPath);
        } else {
            VirtualFile ocamlBinFile = homeDirectory.findFileByRelativePath("bin/ocaml");
            String ocamlBinPath = ocamlBinFile.getPath();
            cmd = new GeneralCommandLine(ocamlBinPath);
        }

        OSProcessHandler handler = new OSProcessHandler(cmd);
        ProcessTerminatedListener.attach(handler, m_environment.getProject());
        return handler;
    }
}
