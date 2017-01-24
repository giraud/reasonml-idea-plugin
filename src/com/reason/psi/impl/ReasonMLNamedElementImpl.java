package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class ReasonMLNamedElementImpl extends ASTWrapperPsiElement implements ReasonMLNamedElement {
    public ReasonMLNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
