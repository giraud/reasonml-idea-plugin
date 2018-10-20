package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiTagClose extends ASTWrapperPsiElement {
    public PsiTagClose(@NotNull ASTNode node) {
        super(node);
    }
}
