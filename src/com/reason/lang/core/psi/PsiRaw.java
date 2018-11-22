package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiRaw extends ASTWrapperPsiElement {

    public PsiRaw(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Raw";
    }
}
