package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLScopedExpr;

public class ReasonMLScopedExprImpl extends ASTWrapperPsiElement implements ReasonMLScopedExpr {

    public ReasonMLScopedExprImpl(ASTNode node) {
        super(node);
    }

}
