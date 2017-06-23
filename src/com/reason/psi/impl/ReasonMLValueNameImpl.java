package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLValueName;

public class ReasonMLValueNameImpl extends ASTWrapperPsiElement implements ReasonMLValueName {

    public ReasonMLValueNameImpl(ASTNode node) {
        super(node);
    }

}
