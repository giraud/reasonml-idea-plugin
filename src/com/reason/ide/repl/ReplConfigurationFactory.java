package com.reason.ide.repl;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ReplConfigurationFactory extends ConfigurationFactory {
    ReplConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ReplRunConfiguration(project, this);
    }

    @Override
    public @NotNull String getId() {
        return getName();
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.REPL;
    }

    @Override
    public @NotNull String getName() {
        return "OCaml REPL configuration factory";
    }
}
