package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiClassConstructor extends ASTWrapperPsiElement {

    public PsiClassConstructor(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }
}

