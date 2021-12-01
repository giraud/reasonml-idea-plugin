package com.reason.ide.debug;

import com.intellij.debugger.impl.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.*;
import com.intellij.execution.runners.*;
import org.jetbrains.annotations.*;

public class OCamlDebugRunner extends GenericProgramRunner<GenericDebuggerRunnerSettings> {
    private static final String OCAML_DEBUG_RUNNER_ID = "OcamlDebugRunner";

    @Override
    public @NotNull String getRunnerId() {
        return OCAML_DEBUG_RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)
                && (profile instanceof OCamlApplicationConfiguration);
    }
}
