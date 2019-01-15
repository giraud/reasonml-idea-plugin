package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OclFileType extends LanguageFileType {
    public static final OclFileType INSTANCE = new OclFileType();

    private OclFileType() {
        super(OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "OCaml file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ml";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.OCL_FILE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
