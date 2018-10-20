package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

public class PsiAssert extends ASTWrapperPsiElement {

    public PsiAssert(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public String toString() {
        return "Assert";
    }

    public PsiElement getAssertion() {
        return getFirstChild().getNextSibling();
    }
}
