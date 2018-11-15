package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiTagBody extends ASTWrapperPsiElement {
    public PsiTagBody(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Tag body";
    }
}
