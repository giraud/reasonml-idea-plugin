package com.reason.bs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.reason.bs.annotations.BsErrorsManager;
import com.reason.bs.compiler.BsCompiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    Collection<BsErrorsManager.BsbInfo> getErrors(String path);

    void clearErrors();

    void addAllInfo(@NotNull Iterable<BsErrorsManager.BsbInfo> bsbInfo);

    @NotNull
    String getNamespace();

    void refmt(@NotNull String format, @NotNull Document document);

    boolean isRefmtOnSaveEnabled();
}
