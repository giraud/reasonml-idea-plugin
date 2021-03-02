package com.reason.ide.console;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BsConsoleFilterProvider implements ConsoleFilterProvider {
    @Override
    public @NotNull Filter[] getDefaultFilters(@NotNull Project project) {
        return new Filter[]{new BsConsoleFilter(project)};
    }
}
