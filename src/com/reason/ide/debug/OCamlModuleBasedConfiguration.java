package com.reason.ide.debug;

import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OCamlModuleBasedConfiguration extends RunConfigurationModule {
    public OCamlModuleBasedConfiguration(@NotNull Project project) {
        super(project);
    }
}
