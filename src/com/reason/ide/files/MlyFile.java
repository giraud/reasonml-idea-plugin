package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.intellij.psi.*;
import com.reason.lang.ocamlyacc.*;
import org.jetbrains.annotations.*;

public class MlyFile extends FileBase {
    public MlyFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclYaccLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return MlyFileType.INSTANCE;
    }

    @Override
    public @NotNull String toString() {
        return MlyFileType.INSTANCE.getDescription();
    }
}
