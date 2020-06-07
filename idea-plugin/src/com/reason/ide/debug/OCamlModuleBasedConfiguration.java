package com.reason.ide.debug;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;

class OCamlModuleBasedConfiguration extends RunConfigurationModule {
    public OCamlModuleBasedConfiguration(@NotNull Project project) {
        super(project);
    }
}
