package com.reason.lang.core.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PsiLetAttribute extends ASTWrapperPsiElement {

    public PsiLetAttribute(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    String getValue() {
        PsiElement nextSibling = getFirstChild().getNextSibling();
        return nextSibling == null ? null : nextSibling.getText();
    }

    @NotNull
    @Override
    public String toString() {
        return "Let attribute";
    }
}
