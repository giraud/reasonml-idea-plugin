package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiLetBinding extends ASTWrapperPsiElement {

    public PsiLetBinding(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Let binding";
    }
}
