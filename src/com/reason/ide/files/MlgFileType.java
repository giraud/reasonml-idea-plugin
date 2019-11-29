package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.extra.OclMlgLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MlgFileType extends LanguageFileType {
    public static final MlgFileType INSTANCE = new MlgFileType();

    private MlgFileType() {
        super(OclMlgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MLG";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml grammar file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mlg";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.OCL_GREEN_FILE;
    }
}
