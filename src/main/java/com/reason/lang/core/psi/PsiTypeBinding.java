package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiTypeBinding extends ASTWrapperPsiElement {

    public PsiTypeBinding(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Type binding";
    }
}
