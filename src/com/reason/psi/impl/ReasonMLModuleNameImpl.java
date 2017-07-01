package com.reason.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.psi.ReasonMLModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReasonMLModuleNameImpl extends RmlNamedElementImpl implements ReasonMLModuleName {

    public ReasonMLModuleNameImpl(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RmlModuleReference(this, getText());
    }

    @Override
    public String toString() {
        return "ModuleName '" + getText() + "'";
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }
}
