package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;

public class PsiFunction extends ASTWrapperPsiElement {

    public PsiFunction(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    public PsiFunctionBody getBody() {
        return PsiTreeUtil.findChildOfType(this, PsiFunctionBody.class);
    }
}