package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.extra.OclMlgLanguage;
import com.reason.ide.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MlgFileType extends LanguageFileType {
  public static final MlgFileType INSTANCE = new MlgFileType();

  private MlgFileType() {
    super(OclMlgLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "MLG";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "OCaml grammar file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "mlg";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.OCL_GREEN_FILE;
  }
}
