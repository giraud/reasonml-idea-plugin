package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;

public class PsiParametersImpl extends MlAstWrapperPsiElement implements PsiParameters {
    public PsiParametersImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public int getArgumentsCount() {
        PsiElement[] children = getChildren();
//        int count = children.length == 0 ? 0 : 1;
//        for (PsiElement child : children) {
//            if (child.getNode().getElementType() == m_types.COMMA) {
//                count++;
//            }
//        }
        return children.length;
    }

    @Override
    public String toString() {
        return "Parameters";
    }
}
