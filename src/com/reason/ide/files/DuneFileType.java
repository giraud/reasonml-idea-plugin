package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.dune.DuneLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneFileType extends LanguageFileType {
    public static final FileType INSTANCE = new DuneFileType();

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
        return "Dune configuration file (jbuilder)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.DUNE_FILE;
    }
}
