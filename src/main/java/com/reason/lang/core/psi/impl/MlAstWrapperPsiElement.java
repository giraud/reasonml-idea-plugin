package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

abstract class MlAstWrapperPsiElement extends ASTWrapperPsiElement {

    protected final ORTypes m_types;

    MlAstWrapperPsiElement(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }
}
