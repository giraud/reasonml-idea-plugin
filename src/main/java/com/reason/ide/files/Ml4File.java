package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ml4File extends FileBase {
    public Ml4File(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return Ml4FileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "OCamlP4 File";
    }

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
