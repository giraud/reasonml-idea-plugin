package com.reason.ide.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.dune.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

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

        VirtualFile duneContentRoot = ORProjectManager.findFirstDuneContentRoot(project);
        VirtualFile baseRoot = duneContentRoot == null ? homeDirectory : duneContentRoot;
        Map<String, String> env = project.getService(OpamEnv.class).getEnv(odk);

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
