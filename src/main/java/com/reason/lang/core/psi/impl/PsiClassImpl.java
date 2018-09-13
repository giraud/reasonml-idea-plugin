package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiClassImpl extends MlAstWrapperPsiElement implements PsiClass {

    //region Constructors
    public PsiClassImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiLowerSymbol.class);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

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
                return Icons.CLASS;
            }
        };
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return getName();
    }

    @Override
    public String toString() {
        return "Class " + getQualifiedName();
    }
}
