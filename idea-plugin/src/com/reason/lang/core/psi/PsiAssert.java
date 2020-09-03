package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

public class PsiAssert extends ASTWrapperPsiElement {

    public PsiAssert(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return "Assert";
    }

    public PsiElement getAssertion() {
        return PsiTreeUtil.skipWhitespacesForward(getFirstChild());
    }
}
