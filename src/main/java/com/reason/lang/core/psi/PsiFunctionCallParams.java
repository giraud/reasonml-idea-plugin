package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiFunctionCallParams extends ASTWrapperPsiElement {

    public PsiFunctionCallParams(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public String toString() {
        return "function call params";
    }
}
