package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiModulePath extends ASTWrapperPsiElement {
    public PsiModulePath(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return "ModulePath";
    }
}
