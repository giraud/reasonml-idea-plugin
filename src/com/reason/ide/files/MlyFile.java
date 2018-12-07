package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.lang.extra.OclMlgLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MlyFile extends FileBase {
    public MlyFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclMlgLanguage.INSTANCE);
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

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
