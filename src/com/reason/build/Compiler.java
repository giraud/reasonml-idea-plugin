package com.reason.build;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.console.CliType;
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
