package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiParameters;
import org.jetbrains.annotations.NotNull;

public class PsiParametersImpl extends MlAstWrapperPsiElement implements PsiParameters {
    public PsiParametersImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public String toString() {
        return "Parameters";
    }
}
