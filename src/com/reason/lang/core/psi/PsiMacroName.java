package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiMacroName extends ASTWrapperPsiElement {

    public PsiMacroName(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return getText();
    }
}
