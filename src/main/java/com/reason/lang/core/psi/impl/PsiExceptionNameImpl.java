package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiExceptionNameImpl extends ASTWrapperPsiElement {
    public PsiExceptionNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "PsiException.name(" + getText() + ")";
    }
}
