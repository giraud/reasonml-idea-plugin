package com.reason.ide.console.rescript;

import com.intellij.execution.filters.*;
import com.intellij.execution.impl.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class RescriptConsoleView extends ConsoleViewImpl {
    public RescriptConsoleView(@NotNull Project project) {
        super(project, true);
    }

    public Filter @NotNull [] getFilters() {
        return new Filter[]{new RescriptOCamlConsoleFilter(getProject())};
    }

    public void disableBuild() {
        // TODO implement method
    }
}
