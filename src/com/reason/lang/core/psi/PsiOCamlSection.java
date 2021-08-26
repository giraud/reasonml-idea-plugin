package com.reason.lang.core.psi;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import org.jetbrains.annotations.*;

public class PsiOCamlSection extends ASTWrapperPsiElement {
    public PsiOCamlSection(ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String toString() {
        return "OCaml lazy section";
    }
}
