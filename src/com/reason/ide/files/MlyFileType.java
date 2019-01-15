package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.extra.OclMlgLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MlyFileType extends LanguageFileType {
    public static final MlyFileType INSTANCE = new MlyFileType();
    static final String EXTENSION = "mly";

    private MlyFileType() {
        super(OclMlgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Mly file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml yacc parser";
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
