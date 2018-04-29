package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PsiSExpr extends ASTWrapperPsiElement {
    public PsiSExpr(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "(";
    }
}
