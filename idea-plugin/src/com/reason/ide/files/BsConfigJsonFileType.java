package com.reason.ide.files;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.vfs.VirtualFile;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BsConfigJsonFileType extends JsonFileType {

  public static final BsConfigJsonFileType INSTANCE = new BsConfigJsonFileType();
  private static final String FILENAME = "bsconfig.json";

  public static boolean isBsConfigFile(@NotNull VirtualFile file) {
    return FILENAME.equals(file.getName());
  }

  @NotNull
  @Override
  public String getName() {
    return "BuckleScript Configuration";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "BuckleScript configuration file";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.BUCKLESCRIPT_TOOL;
  }
}
