package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.RmlNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class RmlNamedElementImpl extends ASTWrapperPsiElement implements RmlNamedElement {
    public RmlNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
