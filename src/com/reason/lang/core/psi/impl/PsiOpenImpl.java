package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiOpenImpl extends PsiToken<ORTypes> implements PsiOpen {

    //region Constructors
    public PsiOpenImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild == null ? "" : ORUtil.getTextUntilTokenType(firstChild, null);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getQualifiedName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.OPEN;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Open " + getQualifiedName();
    }
}
