package com.reason.ide.files;

import static com.reason.dune.DuneConstants.*;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.lang.dune.DuneLanguage;
import icons.ORIcons;
import java.util.Set;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DuneFileType extends LanguageFileType {

  public static final FileType INSTANCE = new DuneFileType();

  public static Set<String> getDefaultFilenames() {
    return ImmutableSet.of(DUNE_FILENAME, DUNE_PROJECT_FILENAME, LEGACY_JBUILDER_FILENAME);
  }

  public static boolean isDuneFile(@NotNull VirtualFile file) {
    return getDefaultFilenames().stream().anyMatch(filename -> filename.equals(file.getName()));
  }

  private DuneFileType() {
    super(DuneLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "DUNE";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Dune configuration file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return ORIcons.DUNE_FILE;
  }
}
