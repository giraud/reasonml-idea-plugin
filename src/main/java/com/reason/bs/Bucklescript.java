package com.reason.bs;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.annotations.BsErrorsManager;
import com.reason.bs.hints.BsQueryTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;

public interface Bucklescript {
    boolean isDependency(String path);

    void refresh();

    void run(FileType fileType);

    @Nullable
    BsCompiler getCompiler();

    @Nullable
    BsCompiler getOrCreateCompiler();

    @Nullable
    BsQueryTypesService.InferredTypes queryTypes(@NotNull Path path);

    @Nullable
    BsQueryTypesService.InferredTypes queryTypes(@NotNull VirtualFile file);

    @Nullable
    Collection<BsErrorsManager.BsbInfo> getErrors(String path);

    void clearErrors();

    void addAllInfo(@NotNull Iterable<BsErrorsManager.BsbInfo> bsbInfo);

    @NotNull
    String getNamespace();
}
