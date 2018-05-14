package com.reason.insight;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.hints.InferredTypes;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public interface InsightManager {

    @FunctionalInterface
    interface ProcessTerminated {
        void run(InferredTypes types);
    }

    @NotNull
    String getRincewindFilename(@NotNull String osPrefix);

    @NotNull
    File getRincewindFile(@NotNull String osPrefix);

    void queryTypes(@NotNull Path path, @NotNull ProcessTerminated callback);

    void queryTypes(@NotNull VirtualFile file, @NotNull ProcessTerminated callback);

}