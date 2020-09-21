package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.napkin.NsLanguage;
import org.jetbrains.annotations.NotNull;

public class NsFile extends FileBase {
  public NsFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, NsLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return NsFileType.INSTANCE;
  }

  @NotNull
  @Override
  public String toString() {
    return getName();
  }
}
