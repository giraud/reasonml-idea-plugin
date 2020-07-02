package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.reason.NsLanguage;

public class NsInterfaceFile extends FileBase {
    public NsInterfaceFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, NsLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return NsInterfaceFileType.INSTANCE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
