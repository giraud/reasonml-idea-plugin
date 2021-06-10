package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public interface Compiler {
    @FunctionalInterface
    interface ProcessTerminated {
        void run();
    }

    @NotNull String getFullVersion(@Nullable VirtualFile file);

    void refresh(@NotNull VirtualFile configFile);

    void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated);

    void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated);

    boolean isConfigured(@NotNull Project project);

    boolean isAvailable(@NotNull Project project);
}
