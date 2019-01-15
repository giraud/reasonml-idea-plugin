package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.extra.OclP4Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Ml4FileType extends LanguageFileType {
    public static final Ml4FileType INSTANCE = new Ml4FileType();

    private Ml4FileType() {
        super(OclP4Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "OCamlP4 file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml preprocessor file";
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
