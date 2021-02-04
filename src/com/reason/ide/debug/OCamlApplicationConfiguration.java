package com.reason.ide.debug;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.options.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OCamlApplicationConfiguration
        extends ModuleBasedConfiguration<OCamlModuleBasedConfiguration, PsiModule> {

    public OCamlApplicationConfiguration(String name, @NotNull OCamlModuleBasedConfiguration configurationModule, @NotNull ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Override
    public @Nullable Collection<Module> getValidModules() {
        return null;
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new CompositeSettingsEditor<RunConfiguration>() {
          @Override
          public CompositeSettingsBuilder<RunConfiguration> getBuilder() {
            return null;
          }
        };
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return null;
    }
}
