package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.extra.OclMlgLanguage;

public class MlgFile extends FileBase {
    public MlgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclMlgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MlgFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return MlgFileType.INSTANCE.getDescription();
    }
}
