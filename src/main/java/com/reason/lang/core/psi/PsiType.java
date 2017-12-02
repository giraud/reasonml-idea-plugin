package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.reason.icons.Icons;
import com.reason.lang.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiType extends ASTWrapperPsiElement {

    public PsiType(ASTNode node) {
        super(node);
    }

    @NotNull
    private PsiElement getTypeConstrElement() {
        return findNotNullChildByType(RmlTypes.TYPE_CONSTR_NAME);
    }

    @Override
    public String getName() {
        return getTypeConstrElement().getText();
    }

    @Nullable
    public PsiScopedExpr getScopedExpression() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return Icons.TYPE;
            }
        };
    }

    @Override
    public String toString() {
        return "Type(" + getName() + ")";
    }
}
