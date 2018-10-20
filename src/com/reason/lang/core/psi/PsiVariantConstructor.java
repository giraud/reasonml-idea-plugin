package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiVariantConstructor extends ASTWrapperPsiElement {

    public PsiVariantConstructor(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Variant constructor";
    }
}
