package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiScopedExpr extends ASTWrapperPsiElement {

    public PsiScopedExpr(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return "Scoped expression";
    }
}
