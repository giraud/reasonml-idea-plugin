package com.reason.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.ReasonMLModule;
import com.reason.psi.ReasonMLModuleName;
import com.reason.psi.ReasonMLScopedExpr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLModuleImpl extends RmlNamedElementImpl implements ReasonMLModule {

    public ReasonMLModuleImpl(ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public ReasonMLScopedExpr getModuleBody() {
        return findChildByClass(ReasonMLScopedExpr.class);
    }

    @Override
    @NotNull
    public ReasonMLModuleName getModuleName() {
        return findNotNullChildByClass(ReasonMLModuleName.class);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getModuleName();
    }

    @Override
    public String getName() {
        return getModuleName().getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        // TODO: Module setName
        return null;
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getModuleName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiFile containingFile = getContainingFile();
                return containingFile == null ? null : containingFile.getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.MODULE;
            }
        };
    }

    @Override
    public String toString() {
        return "Module '" + getName() + "'";
    }
}
