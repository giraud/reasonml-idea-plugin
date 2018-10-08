package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiTry extends ASTWrapperPsiElement {

    private final ORTypes m_types;

    public PsiTry(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiScopedExpr getWith() {
        PsiElement psiElement = ORUtil.nextSiblingWithTokenType(getFirstChild(), m_types.WITH);
        return ORUtil.nextSiblingOfClass(psiElement, PsiScopedExpr.class);
    }

    @Override
    public String toString() {
        return "Try";
    }
}
