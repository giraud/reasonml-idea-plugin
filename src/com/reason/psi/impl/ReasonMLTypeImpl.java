package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLTypeImpl extends ASTWrapperPsiElement implements ReasonMLType {

    public ReasonMLTypeImpl(ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public ReasonMLTypeConstrName getTypeConstrName() {
        return findNotNullChildByClass(ReasonMLTypeConstrName.class);
    }

    @Nullable
    @Override
    public ReasonMLScopedExpr getScopedExpression() {
        return findChildByClass(ReasonMLScopedExpr.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getTypeConstrName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.TYPE;
            }
        };
    }

}
