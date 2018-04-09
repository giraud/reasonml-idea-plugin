package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiSwitch extends ASTWrapperPsiElement {

    public PsiSwitch(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }
}
