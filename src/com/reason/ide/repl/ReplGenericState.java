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
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.OCamlExecutable;
import com.reason.comp.dune.OpamEnv;
import com.reason.ide.ORProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReplGenericState implements RunProfileState {
    private final ExecutionEnvironment m_environment;

    ReplGenericState(ExecutionEnvironment environment) {
        m_environment = environment;
    }

    @Override
    public @Nullable ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        if (processHandler != null) {
            PromptConsoleView consoleView = new PromptConsoleView(m_environment.getProject(), true, true);
            consoleView.attachToProcess(processHandler);

            return new DefaultExecutionResult(consoleView, processHandler);
        }

        return null;
    }

    private @Nullable ProcessHandler startProcess() throws ExecutionException {
        ReplRunConfiguration profile = (ReplRunConfiguration) m_environment.getRunProfile();
        Project project = m_environment.getProject();

        Sdk odk = profile.getSdk();
        VirtualFile homeDirectory = odk == null ? null : odk.getHomeDirectory();
        if (homeDirectory == null) {
            return null;
        }

        VirtualFile baseRoot = ORProjectManager.findFirstDuneContentRoot(project).orElse(homeDirectory);
        Map<String, String> env = ServiceManager.getService(project, OpamEnv.class).getEnv(odk);

        GeneralCommandLine cli = new GeneralCommandLine("ocaml");
        cli.setWorkDirectory(baseRoot.getPath());
        cli.setRedirectErrorStream(true);
        if (env != null) {
            for (Map.Entry<String, String> entry : env.entrySet()) {
                cli.withEnvironment(entry.getKey(), entry.getValue());
            }
        }

        OCamlExecutable executable = OCamlExecutable.getExecutable(odk);
        executable.patchCommandLine(cli, odk.getHomePath() + "/bin", false, project);

        OSProcessHandler handler = new OSProcessHandler(cli);
        ProcessTerminatedListener.attach(handler, project);

        return handler;
    }
}
