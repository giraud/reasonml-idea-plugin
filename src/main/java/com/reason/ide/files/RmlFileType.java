package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.reason.RmlLanguage;
import com.reason.ide.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlFileType extends LanguageFileType {
  public static final RmlFileType INSTANCE = new RmlFileType();

  private RmlFileType() {
    super(RmlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "REASON";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Reason language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "re";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.RML_FILE;
  }

  @NotNull
  @Override
  public String toString() {
    return getName();
  }
}
