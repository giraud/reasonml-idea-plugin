package com.reason.lang.core.psi;

import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiIfStatement extends ASTWrapperPsiElement {

    public PsiIfStatement(ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiBinaryCondition getBinaryCondition() {
        return findChildByClass(PsiBinaryCondition.class);
    }
}

