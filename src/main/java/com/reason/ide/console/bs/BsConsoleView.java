package com.reason.ide.console.bs;

import com.intellij.execution.impl.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class BsConsoleView extends ConsoleViewImpl {
    public BsConsoleView(@NotNull Project project) {
        super(project, true);
    }
}
