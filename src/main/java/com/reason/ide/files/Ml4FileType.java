package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.icons.Icons;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Ml4FileType extends LanguageFileType {
    public static final Ml4FileType INSTANCE = new Ml4FileType();

    private Ml4FileType() {
        super(OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "OcamlP4 file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ocaml preprocessor file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ml4";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.OCL_GREEN_FILE;
    }
}
