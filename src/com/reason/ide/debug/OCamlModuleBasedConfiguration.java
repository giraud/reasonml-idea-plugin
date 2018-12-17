package com.reason.ide.debug;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

public class OCamlModuleBasedConfiguration extends RunConfigurationModule {
    public OCamlModuleBasedConfiguration(Project project) {
        super(project);
    }
}
