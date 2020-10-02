package com.reason.ide.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class ReplRunConfigurationType implements ConfigurationType {
  @NotNull
  @Override
  public String getDisplayName() {
    return "OCaml REPL";
  }

  @NotNull
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

  @NotNull
  @Override
  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[] {new ReplConfigurationFactory(this)};
  }
}
