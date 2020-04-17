package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import icons.ORIcons;
import com.reason.lang.dune.DuneLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneFileType extends LanguageFileType {

    public static final FileType INSTANCE = new DuneFileType();

    public static String getDefaultFilename() {
        return "dune-project";
    }

    private DuneFileType() {
        super(DuneLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Dune configuration";
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
