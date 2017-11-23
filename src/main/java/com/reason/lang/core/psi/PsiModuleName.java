package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiModuleName extends ASTWrapperPsiElement {

    public PsiModuleName(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "ModuleName '" + getText() + "'";
    }
}
