package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiInterpolation extends ASTWrapperPsiElement {

    public PsiInterpolation(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }
}

