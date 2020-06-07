package com.reason.ide.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.ui.LayeredIcon;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReplConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "OCaml REPL configuration factory";

    ReplConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ReplRunConfiguration(project, this, "OCaml repl");
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return LayeredIcon.create(ORIcons.OCL_FILE, ORIcons.OVERLAY_EXECUTE);
    }

    @NotNull
    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
