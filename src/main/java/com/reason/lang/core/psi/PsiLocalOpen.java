package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

public class PsiLocalOpen extends ASTWrapperPsiElement {

    private final MlTypes m_types;

    public PsiLocalOpen(ASTNode node, MlTypes types) {
        super(node);
        m_types = types;
    }

    @NotNull
    public String getName() {
        PsiElement firstChild = getFirstChild();
        StringBuilder sb = new StringBuilder(firstChild.getText());

        PsiElement nextSibling = firstChild.getNextSibling();
        while (nextSibling.getNode().getElementType() != m_types.SCOPED_EXPR) {
            sb.append(nextSibling.getText());
            nextSibling = nextSibling.getNextSibling();
        }

        String name = sb.toString();
        return name.substring(0, name.length() - 1);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public String toString() {
        return "Local open " + getName();
    }
}
