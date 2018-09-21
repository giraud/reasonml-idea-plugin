package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiClassImpl extends MlAstWrapperPsiElement implements PsiClass {

    //region Constructors
    public PsiClassImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
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

    @Nullable
    @Override
    public String getQualifiedName() {
        return getName();
    }

    @Nullable
    @Override
    public PsiElement getClassBody() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @NotNull
    @Override
    public Collection<PsiClassField> getFields() {
        return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassField.class);
    }

    @NotNull
    @Override
    public Collection<PsiClassMethod> getMethods() {
        return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassMethod.class);
    }

    @NotNull
    @Override
    public Collection<PsiClassParameters> getParameters() {
        return PsiTreeUtil.findChildrenOfType(this, PsiClassParameters.class);
    }

    @Nullable
    @Override
    public PsiClassConstructor getConstructor() {
        return findChildByClass(PsiClassConstructor.class);
    }

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

    @Override
    public String toString() {
        return "Class " + getQualifiedName();
    }
}
