package com.reason.ide.console;

import com.intellij.execution.filters.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.console.esy.*;
import org.jetbrains.annotations.*;

public class ORConsoleFilterProvider extends ConsoleDependentFilterProvider {
    @Override
    public @NotNull Filter[] getDefaultFilters(@NotNull ConsoleView consoleView, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        if (consoleView instanceof DuneConsoleView) {
            return ((DuneConsoleView) consoleView).getFilters();
        }
        if (consoleView instanceof EsyConsoleView) {
            return ((EsyConsoleView) consoleView).getFilters();
        }
        return super.getDefaultFilters(project);
    }
}
