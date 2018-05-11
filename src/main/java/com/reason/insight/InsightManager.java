package com.reason.insight;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.hints.BsQueryTypesService;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface InsightManager {

    @FunctionalInterface
    interface ProcessTerminated {
        void run(BsQueryTypesService.InferredTypes types);
    }

    void queryTypes(@NotNull Path path, @NotNull ProcessTerminated callback);

    void queryTypes(@NotNull VirtualFile file, @NotNull ProcessTerminated callback);

}
