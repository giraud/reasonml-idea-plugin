package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.extra.OclMllLanguage;

public class MllFile extends FileBase {
    public MllFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclMllLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MllFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return MllFileType.INSTANCE.getDescription();
    }
}
