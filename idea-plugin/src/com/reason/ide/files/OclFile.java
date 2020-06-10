package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocaml.OclLanguage;

public class OclFile extends FileBase {
    public OclFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return OclFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
