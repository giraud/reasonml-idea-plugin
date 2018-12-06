package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.icons.Icons;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OclInterfaceFileType extends LanguageFileType {
    public static final OclInterfaceFileType INSTANCE = new OclInterfaceFileType();

    private OclInterfaceFileType() {
        super(OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "OCaml interface file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml language interface file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mli";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.OCL_BLUE_FILE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
