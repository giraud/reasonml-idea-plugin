package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;

public class PsiModuleName extends ASTWrapperPsiElement {

    public PsiModuleName(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new PsiModuleReference(this, getText());
    }

    @Override
    public String toString() {
        return "ModuleName '" + getText() + "'";
    }
}
