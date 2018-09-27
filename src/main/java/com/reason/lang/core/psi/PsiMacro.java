package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiMacro extends ASTWrapperPsiElement {

    public PsiMacro(ASTNode node) {
        super(node);
    }

}
