package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocamlyacc.OclYaccLanguage;

public class MlyFile extends FileBase {
    public MlyFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclYaccLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MlyFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return MlyFileType.INSTANCE.getDescription();
    }
}
