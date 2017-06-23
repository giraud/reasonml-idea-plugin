package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLLetBinding;

public class ReasonMLLetBindingImpl extends ASTWrapperPsiElement implements ReasonMLLetBinding {

    public ReasonMLLetBindingImpl(ASTNode node) {
        super(node);
    }

}
