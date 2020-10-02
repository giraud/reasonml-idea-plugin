package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.napkin.NsLanguage;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NsInterfaceFileType extends LanguageFileType {
  public static final NsInterfaceFileType INSTANCE = new NsInterfaceFileType();

  private NsInterfaceFileType() {
    super(NsLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "RESCRIPT_INTF";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Rescript language interface file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "resi";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.NS_INTERFACE_FILE;
  }

  @NotNull
  @Override
  public String toString() {
    return getName();
  }
}
