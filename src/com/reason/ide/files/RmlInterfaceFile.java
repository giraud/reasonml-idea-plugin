package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlInterfaceFile extends FileBase {
    public RmlInterfaceFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, RmlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RmlInterfaceFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return getName();
    }

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
