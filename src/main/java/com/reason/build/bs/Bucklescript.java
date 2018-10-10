package com.reason.build.bs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.Compiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Bucklescript extends Compiler {

    boolean isDependency(@Nullable VirtualFile file);

    @NotNull
    String getNamespace(@NotNull VirtualFile sourceFile);

    void convert(@NotNull VirtualFile virtualFile, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document);

    void refmt(@NotNull VirtualFile sourceFile, @NotNull String format, @NotNull Document document);

    boolean isRefmtOnSaveEnabled();
}
