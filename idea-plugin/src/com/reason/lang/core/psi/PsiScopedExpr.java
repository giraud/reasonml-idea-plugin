package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiScopedExpr extends ASTWrapperPsiElement {

    @NotNull
    private final ORTypes m_types;

    public PsiScopedExpr(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public boolean isEmpty() {
        PsiElement firstChild = getFirstChild();
        IElementType firstType = firstChild == null ? null : firstChild.getNode().getElementType();
        if (firstType == m_types.LPAREN) {
            assert firstChild != null;
            PsiElement secondChild = firstChild.getNextSibling();
            IElementType secondType = secondChild == null ? null : secondChild.getNode().getElementType();
            return secondType == m_types.RPAREN;
        }

        return false;
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return "Scoped expression";
    }
}
