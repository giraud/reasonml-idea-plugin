package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.ocaml.OclLanguage;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclInterfaceFileType extends LanguageFileType {
  public static final OclInterfaceFileType INSTANCE = new OclInterfaceFileType();

  private OclInterfaceFileType() {
    super(OclLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "OCAML_INTF";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "OCaml language interface file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "mli";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.OCL_INTERFACE_FILE;
  }

  @NotNull
  @Override
  public String toString() {
    return getName();
  }
}
