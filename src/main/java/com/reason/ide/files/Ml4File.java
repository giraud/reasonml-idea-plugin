package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.extra.OclP4Language;
import org.jetbrains.annotations.NotNull;

public class Ml4File extends PsiFileBase {
  public Ml4File(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, OclP4Language.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return Ml4FileType.INSTANCE;
  }

  @NotNull
  @Override
  public String toString() {
    return Ml4FileType.INSTANCE.getDescription();
  }
}
