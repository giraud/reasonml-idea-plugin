package com.reason.ide.console;

import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public abstract class ORConsoleFilter implements Filter {
    protected static final Log LOG = Log.create("console");

    protected final Project myProject;

    protected ORConsoleFilter(@NotNull Project project) {
        myProject = project;
    }

    protected abstract @Nullable OpenFileHyperlinkInfo getHyperlinkInfo(String filePath, int documentLine, int documentColumn);
}
