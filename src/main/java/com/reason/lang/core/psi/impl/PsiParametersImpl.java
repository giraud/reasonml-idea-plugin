package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiParametersImpl extends PsiToken<ORTypes> implements PsiParameters {
    public PsiParametersImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public int getArgumentsCount() {
        PsiElement[] children = getChildren();
        return children.length;
    }

    @Override
    public String toString() {
        return "Parameters";
    }
}
