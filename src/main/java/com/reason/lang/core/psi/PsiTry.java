package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiTry extends ASTWrapperPsiElement {

    public PsiTry(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "Try";
    }
}
