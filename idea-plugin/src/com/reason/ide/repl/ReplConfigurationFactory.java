package com.reason.ide.repl;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class ReplConfigurationFactory extends ConfigurationFactory {
  ReplConfigurationFactory(@NotNull ConfigurationType type) {
    super(type);
  }

  @NotNull
  @Override
  public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new ReplRunConfiguration(project, this, "OCaml REPL");
  }

  @Override
  public @NotNull String getId() {
    return getName();
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return ORIcons.REPL;
  }

  @NotNull
  @Override
  public String getName() {
    return "OCaml REPL configuration factory";
  }
}
