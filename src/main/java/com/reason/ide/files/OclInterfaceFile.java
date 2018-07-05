package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class OclInterfaceFile extends FileBase {
    public OclInterfaceFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return OclInterfaceFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return getName();
    }
}
