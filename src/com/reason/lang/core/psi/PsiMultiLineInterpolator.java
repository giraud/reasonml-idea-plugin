package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiMultiLineInterpolator extends ASTWrapperPsiElement {
    public PsiMultiLineInterpolator(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "MultiLine interpolator";
    }
}