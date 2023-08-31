package com.reason.comp.bs;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler;
import org.jetbrains.annotations.*;

public interface BsCompiler extends Compiler {
    @NotNull
    String getNamespace(@NotNull VirtualFile sourceFile);

    @Nullable
    String convert(@Nullable VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document);

    @NotNull
    Ninja readNinjaBuild(@Nullable VirtualFile contentRoot);

    void refreshNinjaBuild();
}
