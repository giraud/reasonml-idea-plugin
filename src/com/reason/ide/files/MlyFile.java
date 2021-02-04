package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocamlyacc.OclYaccLanguage;
import org.jetbrains.annotations.NotNull;

public class MlyFile extends FileBase {
  public MlyFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, OclYaccLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return MlyFileType.INSTANCE;
  }

  @NotNull
  @Override
  public String toString() {
    return MlyFileType.INSTANCE.getDescription();
  }
}
