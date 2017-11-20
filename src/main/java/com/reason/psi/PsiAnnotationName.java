package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiAnnotationName extends ASTWrapperPsiElement {

    public PsiAnnotationName(ASTNode node) {
        super(node);
    }

}
