package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class OclFile extends FileBase {
    public OclFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return OclFileType.INSTANCE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
