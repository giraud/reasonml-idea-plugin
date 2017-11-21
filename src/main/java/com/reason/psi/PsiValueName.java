package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PsiValueName extends ASTWrapperPsiElement {

    PsiValueName(ASTNode node) {
        super(node);
    }

    public String getValue() {
        PsiElement firstChild = getFirstChild();
        return firstChild != null ? firstChild.getText() : "";
    }
}
