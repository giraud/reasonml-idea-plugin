package com.reason.bs;

import com.intellij.openapi.editor.Document;
import com.reason.Compiler;
import com.reason.bs.annotations.BsErrorsManager;
import com.reason.bs.compiler.BsCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Bucklescript extends Compiler {
    boolean isDependency(String path);

    @Nullable
    BsCompiler getCompiler();

    @Nullable
    BsCompiler getOrCreateCompiler();

    @Nullable
    Collection<BsErrorsManager.BsbInfo> getErrors(String path);

    void clearErrors();

    void addAllInfo(@NotNull Iterable<BsErrorsManager.BsbInfo> bsbInfo);

    @NotNull
    String getNamespace();

    void refmt(@NotNull String format, @NotNull Document document);

    boolean isRefmtOnSaveEnabled();
}
