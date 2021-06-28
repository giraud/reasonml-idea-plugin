package com.reason.ide.files;

import static com.reason.comp.esy.EsyConstants.ESY_CONFIG_FILENAME;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EsyPackageJsonFileType extends JsonFileType {

  public static final FileType INSTANCE = new EsyPackageJsonFileType();

  public static @NotNull String getDefaultFilename() {
    return ESY_CONFIG_FILENAME;
  }

  private EsyPackageJsonFileType() {}

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.ESY_FILE;
  }
}
