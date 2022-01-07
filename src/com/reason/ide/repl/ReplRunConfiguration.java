package com.reason.ide.repl;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.*;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class ReplRunConfiguration extends RunConfigurationBase<ReplRunConfiguration> {
    ReplRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
        super(project, factory, "OCaml REPL");
    }

    @Override
    public @NotNull SettingsEditor<ReplRunConfiguration> getConfigurationEditor() {
        return new ReplRunSettingsEditor(getProject());
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new ReplGenericState(executionEnvironment);
    }
}
