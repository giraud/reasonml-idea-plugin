package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocamllex.OclLexLanguage;
import org.jetbrains.annotations.NotNull;

public class MllFile extends PsiFileBase {
    public MllFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLexLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return MllFileType.INSTANCE;
    }

    @Override
    public @NotNull String toString() {
        return MllFileType.INSTANCE.getDescription();
    }
}
