package com.reason.ide.debug;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.GenericProgramRunner;
import org.jetbrains.annotations.NotNull;

public class OCamlDebugRunner extends GenericProgramRunner {

  private static final String OCAML_DEBUG_RUNNER_ID = "OcamlDebugRunner";

  @NotNull
  @Override
  public String getRunnerId() {
    return OCAML_DEBUG_RUNNER_ID;
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)
        && (profile instanceof OCamlApplicationConfiguration);
  }
}
