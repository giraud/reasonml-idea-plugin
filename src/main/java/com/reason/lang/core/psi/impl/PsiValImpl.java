package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiValImpl extends MlAstWrapperPsiElement implements PsiVal {

    //region Constructors
    public PsiValImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(m_types.VALUE_NAME);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

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

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.VAL;
            }
        };
    }

    @Override
    public String toString() {
        return "PsiVal(" + getName() + ")";
    }
}
