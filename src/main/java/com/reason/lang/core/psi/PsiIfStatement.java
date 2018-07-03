package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

import javax.annotation.Nullable;

public class PsiIfStatement extends ASTWrapperPsiElement {

    public PsiIfStatement(ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiBinaryCondition getBinaryCondition() {
        return findChildByClass(PsiBinaryCondition.class);
    }
}

