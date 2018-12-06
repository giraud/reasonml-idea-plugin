package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiExceptionImpl extends PsiToken<ORTypes> implements PsiException {

    public PsiExceptionImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiUpperSymbol.class);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.EXCEPTION;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Exception " + getName();
    }
}
