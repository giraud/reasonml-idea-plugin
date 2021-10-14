package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class OclInterfaceFile extends FileBase {
    public OclInterfaceFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return OclInterfaceFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
