package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.extra.OclMlgLanguage;
import org.jetbrains.annotations.NotNull;

public class MlgFile extends PsiFileBase {
  public MlgFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, OclMlgLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return MlgFileType.INSTANCE;
  }

  @NotNull
  @Override
  public String toString() {
    return MlgFileType.INSTANCE.getDescription();
  }
}
