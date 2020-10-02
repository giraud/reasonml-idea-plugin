package com.reason.bs;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BsCompiler extends Compiler {

  boolean isDependency(@Nullable VirtualFile file);

  @NotNull
  String getNamespace(@NotNull VirtualFile sourceFile);

  @Nullable
  String convert(
      @NotNull VirtualFile virtualFile,
      boolean isInterface,
      @NotNull String fromFormat,
      @NotNull String toFormat,
      @NotNull Document document);

  void refmt(
      @NotNull VirtualFile sourceFile,
      boolean isInterface,
      @NotNull String format,
      @NotNull Document document);

  @NotNull
  Ninja readNinjaBuild(@Nullable VirtualFile contentRoot);
}
