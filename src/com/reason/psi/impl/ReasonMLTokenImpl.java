package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLToken;

public class ReasonMLTokenImpl extends ASTWrapperPsiElement implements ReasonMLToken {

    public ReasonMLTokenImpl(ASTNode node) {
        super(node);
    }

}
