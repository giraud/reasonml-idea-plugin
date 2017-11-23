package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiScopedExpr extends ASTWrapperPsiElement {

    public PsiScopedExpr(ASTNode node) {
        super(node);
    }

}
