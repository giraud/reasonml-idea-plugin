package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;

public class PsiTry extends ASTWrapperPsiElement {

    private final MlTypes m_types;

    public PsiTry(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiScopedExpr getWith() {
        PsiElement psiElement = PsiUtil.nextSiblingWithTokenType(getFirstChild(), m_types.WITH);
        return PsiUtil.nextSiblingOfClass(psiElement, PsiScopedExpr.class);
    }

    @Override
    public String toString() {
        return "Try";
    }
}
