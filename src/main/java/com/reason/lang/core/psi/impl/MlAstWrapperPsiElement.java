package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

abstract class MlAstWrapperPsiElement extends ASTWrapperPsiElement {

    protected final MlTypes m_types;

    MlAstWrapperPsiElement(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }
}
