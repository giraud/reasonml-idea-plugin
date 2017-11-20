package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiValueName extends ASTWrapperPsiElement {

    PsiValueName(ASTNode node) {
        super(node);
    }

    public String getValue() {
        return getFirstChild().getText();
    }
}
