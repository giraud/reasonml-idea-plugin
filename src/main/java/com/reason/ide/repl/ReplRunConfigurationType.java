package com.reason.ide.repl;

import com.intellij.execution.configurations.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ReplRunConfigurationType implements ConfigurationType {
    @Override
    public @NotNull String getDisplayName() {
        return "OCaml REPL";
    }

    @Override
    public @NotNull String getConfigurationTypeDescription() {
        return "OCaml REPL configuration Type";
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.OCAML;
    }

    @Override
    public @NotNull String getId() {
        return "OCAML_RUN_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory @NotNull [] getConfigurationFactories() {
        return new ConfigurationFactory[]{new ReplConfigurationFactory(this)};
    }
}
