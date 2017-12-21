package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.PsiSignature;

public class PsiSignatureImpl extends ASTWrapperPsiElement implements PsiSignature {
    public PsiSignatureImpl(ASTNode node) {
        super(node);
    }
}
