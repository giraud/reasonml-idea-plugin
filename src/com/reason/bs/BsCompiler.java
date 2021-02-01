package com.reason.bs;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.vfs.*;
import com.reason.Compiler;
import org.jetbrains.annotations.*;

public interface BsCompiler extends Compiler {

  boolean isDependency(@Nullable VirtualFile file);

  @NotNull
  String getNamespace(@NotNull VirtualFile sourceFile);

  @Nullable
  String convert(@NotNull VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document);

  @NotNull
  Ninja readNinjaBuild(@Nullable VirtualFile contentRoot);

  void refreshNinjaBuild();
}
