package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;

public class RmlInterfaceFile extends FileBase {
  public RmlInterfaceFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, RmlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return RmlInterfaceFileType.INSTANCE;
  }

  @NotNull
  @Override
  public String toString() {
    return getName();
  }
}
