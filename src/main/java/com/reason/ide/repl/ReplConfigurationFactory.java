package com.reason.ide.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class ReplConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_NAME = "OCaml REPL configuration factory";

    protected ReplConfigurationFactory(ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new ReplRunConfiguration(project, this, "OCaml repl");
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }
}
