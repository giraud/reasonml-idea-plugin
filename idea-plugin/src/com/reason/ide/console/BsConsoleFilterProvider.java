package com.reason.ide.console;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BsConsoleFilterProvider implements ConsoleFilterProvider {
  @NotNull
  @Override
  public Filter @NotNull [] getDefaultFilters(@NotNull Project project) {
    return new Filter[] {new BsConsoleFilter(project)};
  }
}
