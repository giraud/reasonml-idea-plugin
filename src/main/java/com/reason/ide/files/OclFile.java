package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class OclFile extends PsiFileBase {
    public OclFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return OclFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Ocaml File";
    }
}
