package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.extra.OclP4Language;

public class Ml4File extends FileBase {
    public Ml4File(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclP4Language.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Ml4FileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return Ml4FileType.INSTANCE.getDescription();
    }
}
