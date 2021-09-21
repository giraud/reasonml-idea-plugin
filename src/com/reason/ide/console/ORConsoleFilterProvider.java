package com.reason.ide.console;

import com.intellij.execution.filters.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.console.esy.*;
import com.reason.ide.console.rescript.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

/**
 * Provide a filter for console output, for ex to extract specific file location for linking.
 * Default implementation extract basic url, like C:\xxx\source.re:row:col
 */
public class ORConsoleFilterProvider extends ConsoleDependentFilterProvider {
    @Override
    public @NotNull Filter[] getDefaultFilters(@NotNull ConsoleView consoleView, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        if (consoleView instanceof RescriptConsoleView) {
            return project.getService(ORSettings.class).isUseSuperErrors()
                    ? super.getDefaultFilters(project)
                    : ((RescriptConsoleView) consoleView).getFilters();
        }
        if (consoleView instanceof DuneConsoleView) {
            return ((DuneConsoleView) consoleView).getFilters();
        }
        if (consoleView instanceof EsyConsoleView) {
            return ((EsyConsoleView) consoleView).getFilters();
        }
        return super.getDefaultFilters(project);
    }
}
