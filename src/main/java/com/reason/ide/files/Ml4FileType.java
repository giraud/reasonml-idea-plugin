package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.extra.OclP4Language;
import com.reason.ide.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ml4FileType extends LanguageFileType {
  public static final Ml4FileType INSTANCE = new Ml4FileType();

  private Ml4FileType() {
    super(OclP4Language.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "OCamlP4 file";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "OCaml preprocessor file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "ml4";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.OCL_GREEN_FILE;
  }
}
