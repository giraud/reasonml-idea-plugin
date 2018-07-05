package com.reason.build.bs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.reason.build.Compiler;
import com.reason.build.bs.compiler.BsCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Bucklescript extends Compiler {
    boolean isDependency(@Nullable String path);

    boolean isDependency(@Nullable PsiFile file);

    @Nullable
    BsCompiler getCompiler();

    @Nullable
    BsCompiler getOrCreateCompiler();

    @NotNull
    String getNamespace();

    void convert(@NotNull VirtualFile virtualFile, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document);

    void refmt(@NotNull String format, @NotNull Document document);

    boolean isRefmtOnSaveEnabled();
}
