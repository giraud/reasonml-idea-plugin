package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiToken<T> extends ASTWrapperPsiElement {

    protected final T m_types;

    public PsiToken(@NotNull T types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }
}
