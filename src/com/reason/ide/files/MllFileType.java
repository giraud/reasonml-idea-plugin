package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.extra.OclMllLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MllFileType extends LanguageFileType {
    public static final MllFileType INSTANCE = new MllFileType();
    static final String EXTENSION = "mll";

    private MllFileType() {
        super(OclMllLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MLL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml lexer";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.OCL_GREEN_FILE;
    }
}
