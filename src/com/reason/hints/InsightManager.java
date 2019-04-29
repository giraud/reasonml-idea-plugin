package com.reason.hints;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.hints.InferredTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

public interface InsightManager {

    @FunctionalInterface
    interface ProcessTerminated {
        void run(InferredTypes types);
    }

    boolean useCmt();

    void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile);

    @Nullable
    File getRincewindFile(@NotNull VirtualFile sourceFile);

    @Nullable
    String getRincewindFilename(@NotNull VirtualFile sourceFile);

    void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path path, @NotNull ProcessTerminated callback);

}
