package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiVariantConstructor extends ASTWrapperPsiElement {

    public PsiVariantConstructor(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String toString() {
        return "Variant constructor";
    }
}
