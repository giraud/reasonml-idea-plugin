package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLFunBody;

public class ReasonMLFunBodyImpl extends ASTWrapperPsiElement implements ReasonMLFunBody {

    public ReasonMLFunBodyImpl(ASTNode node) {
        super(node);
    }

}
