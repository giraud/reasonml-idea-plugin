package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.rescript.ResLanguage;
import org.jetbrains.annotations.NotNull;

public class ResFile extends FileBase {
    public ResFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ResLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return ResFileType.INSTANCE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
