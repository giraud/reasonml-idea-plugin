package com.reason.ide.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.ocaml.*;
import com.reason.ide.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ReplGenericState implements RunProfileState {
    private final ExecutionEnvironment myEnvironment;

    ReplGenericState(ExecutionEnvironment environment) {
        myEnvironment = environment;
    }

    @Override
    public @Nullable ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
        ProcessHandler processHandler = startProcess();
        if (processHandler != null) {
            PromptConsoleView consoleView = new PromptConsoleView(myEnvironment.getProject(), true, true);
            consoleView.attachToProcess(processHandler);

            return new DefaultExecutionResult(consoleView, processHandler);
        }

        return null;
    }

    private @Nullable ProcessHandler startProcess() throws ExecutionException {
        //ReplRunConfiguration profile = (ReplRunConfiguration) myEnvironment.getRunProfile();
        Project project = myEnvironment.getProject();
        ORSettings settings = project.getService(ORSettings.class);

        String opamLocation = settings == null ? null : settings.getOpamLocation();
        String switchName = settings == null ? null : settings.getSwitchName();
        if (opamLocation == null || switchName == null) {
            return null;
        }

        VirtualFile baseRoot = ORProjectManager.findFirstDuneContentRoot(project);

        GeneralCommandLine cli = new GeneralCommandLine("ocaml");
        cli.setRedirectErrorStream(true);
        if (baseRoot != null) {
            cli.setWorkDirectory(baseRoot.getPath());
        }

        Map<String, String> env = project.getService(OpamEnv.class).getEnv(switchName);
        if (env != null) {
            for (Map.Entry<String, String> entry : env.entrySet()) {
                cli.withEnvironment(entry.getKey(), entry.getValue());
            }
        }

        OCamlExecutable executable = OCamlExecutable.getExecutable(opamLocation, null);
        executable.patchCommandLine(cli, opamLocation + "/" + switchName + "/bin", false, project);

        OSProcessHandler handler = new OSProcessHandler(cli);
        ProcessTerminatedListener.attach(handler, project);

        return handler;
    }
}
