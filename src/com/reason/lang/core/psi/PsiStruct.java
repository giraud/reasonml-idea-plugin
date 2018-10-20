package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiStruct extends ASTWrapperPsiElement {
    public PsiStruct(ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Struct";
    }
}