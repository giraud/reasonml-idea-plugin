package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.reason.lang.ocaml.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class OclInterfaceFileType extends LanguageFileType {
    public static final OclInterfaceFileType INSTANCE = new OclInterfaceFileType();

    private OclInterfaceFileType() {
        super(OclLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "OCAML_INTF";
    }

    @Override public @NotNull @Nls String getDisplayName() {
        return "OCaml interface";
    }

    @Override
    public @NotNull String getDescription() {
        return "OCaml language interface file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "mli";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.OCL_INTERFACE_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
