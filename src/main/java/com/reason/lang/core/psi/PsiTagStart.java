package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiTagStart extends ASTWrapperPsiElement {
    public PsiTagStart(@NotNull ASTNode node) {
        super(node);
    }
}
