package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiLetBinding extends ASTWrapperPsiElement {

    public PsiLetBinding(ASTNode node) {
        super(node);
    }

}
