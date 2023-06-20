package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.ocamlgrammar.*;
import org.jetbrains.annotations.NotNull;

public class MlgFile extends PsiFileBase {
    public MlgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclGrammarLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return MlgFileType.INSTANCE;
    }

    @Override
    public @NotNull String toString() {
        return MlgFileType.INSTANCE.getDescription();
    }
}
