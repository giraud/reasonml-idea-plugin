package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.ocamlyacc.OclYaccLanguage;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MlyFileType extends LanguageFileType {
  public static final MlyFileType INSTANCE = new MlyFileType();
  static final String EXTENSION = "mly";

  private MlyFileType() {
    super(OclYaccLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "Mly file";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "OCaml yacc parser";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.OCL_GREEN_FILE;
  }
}
