package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.lang.extra.OclP4Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
