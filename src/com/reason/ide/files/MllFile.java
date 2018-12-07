package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.lang.extra.OclMllLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
