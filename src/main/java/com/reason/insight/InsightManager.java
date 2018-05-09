package com.reason.insight;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.hints.BsQueryTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface InsightManager {

    @Nullable
    BsQueryTypesService.InferredTypes queryTypes(@NotNull Path path);

    @Nullable
    BsQueryTypesService.InferredTypes queryTypes(@NotNull VirtualFile file);

}
