package com.reason.ide.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReplRunConfigurationType implements ConfigurationType {
    @Override
    public String getDisplayName() {
        return "OCaml REPL";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "OCaml REPL configuration Type";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.General.Information;
    }

    @NotNull
    @Override
    public String getId() {
        return "DEMO_RUN_CONFIGURATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new ReplConfigurationFactory(this)};
    }
}
