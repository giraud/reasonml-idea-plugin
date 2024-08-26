package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.ide.ORIcons;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclFileType extends LanguageFileType {
    public static final OclFileType INSTANCE = new OclFileType();

    private OclFileType() {
        super(OclLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "OCAML";
    }

    @Override
    public @NotNull String getDescription() {
        return "OCaml language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "ml";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.OCL_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
