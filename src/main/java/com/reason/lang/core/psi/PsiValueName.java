package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PsiValueName extends ASTWrapperPsiElement {

    public PsiValueName(ASTNode node) {
        super(node);
    }

    public String getName() {
        PsiElement firstChild = getFirstChild();
        return firstChild != null ? firstChild.getText() : "";
    }
}
