package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.reason.icons.ReasonMLIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLType extends ASTWrapperPsiElement {

    public ReasonMLType(ASTNode node) {
        super(node);
    }

    @NotNull
    public ReasonMLTypeConstrName getTypeConstrName() {
        return findNotNullChildByClass(ReasonMLTypeConstrName.class);
    }

    @Nullable
    public ReasonMLScopedExpr getScopedExpression() {
        return findChildByClass(ReasonMLScopedExpr.class);
    }

    @Override
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
