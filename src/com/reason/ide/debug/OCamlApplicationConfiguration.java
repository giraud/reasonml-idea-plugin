package com.reason.ide.debug;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.reason.lang.core.psi.PsiModule;

public class OCamlApplicationConfiguration extends ModuleBasedConfiguration<OCamlModuleBasedConfiguration, PsiModule> {

    public OCamlApplicationConfiguration(String name, @NotNull OCamlModuleBasedConfiguration configurationModule, @NotNull ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Nullable
    @Override
    public Collection<Module> getValidModules() {
        return null;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return null;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return null;
    }
}
