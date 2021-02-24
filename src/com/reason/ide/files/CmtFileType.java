package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CmtFileType implements FileType {
  public static final CmtFileType INSTANCE = new CmtFileType();

  @NotNull
  @Override
  public String getName() {
    return "CMT";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Cmt";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "cmt";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return UnknownFileType.INSTANCE.getIcon();
  }

  @Override
  public boolean isBinary() {
    return true;
  }

  @Override
  public boolean isReadOnly() {
    return true;
  }

  @Nullable
  @Override
  public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
    return null;
  }
}
