package com.reason;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Compiler {

    @FunctionalInterface
    interface ProcessTerminated {
        void run();
    }

    void refresh(@NotNull VirtualFile bsconfigFile);

    void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated);

}
