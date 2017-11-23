package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiType extends ASTWrapperPsiElement {

    public PsiType(ASTNode node) {
        super(node);
    }

    @NotNull
    private PsiTypeConstrName getTypeConstrName() {
        return findNotNullChildByClass(PsiTypeConstrName.class);
    }

    @Nullable
    public PsiScopedExpr getScopedExpression() {
        return findChildByClass(PsiScopedExpr.class);
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
                return Icons.TYPE;
            }
        };
    }

}
