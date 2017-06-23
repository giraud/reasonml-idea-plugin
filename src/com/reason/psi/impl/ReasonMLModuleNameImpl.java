package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLModuleName;

public class ReasonMLModuleNameImpl extends ASTWrapperPsiElement implements ReasonMLModuleName {

    public ReasonMLModuleNameImpl(ASTNode node) {
        super(node);
    }

}
