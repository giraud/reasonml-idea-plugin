package com.reason.ide.console.dune;

import com.intellij.execution.filters.*;
import com.intellij.execution.impl.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class DuneConsoleView extends ConsoleViewImpl {
    public DuneConsoleView(@NotNull Project project) {
        super(project, true);
    }

    public Filter[] getFilters() {
        return new Filter[]{new OCamlConsoleFilter(getProject())};
    }
}
