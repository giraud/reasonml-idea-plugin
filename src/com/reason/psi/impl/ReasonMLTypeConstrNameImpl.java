package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLTypeConstrName;

public class ReasonMLTypeConstrNameImpl extends ASTWrapperPsiElement implements ReasonMLTypeConstrName {

    public ReasonMLTypeConstrNameImpl(ASTNode node) {
        super(node);
    }


}
