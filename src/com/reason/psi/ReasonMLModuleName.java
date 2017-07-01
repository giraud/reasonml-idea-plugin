package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.reason.psi.impl.RmlModuleReference;

public class ReasonMLModuleName extends ASTWrapperPsiElement {

    public ReasonMLModuleName(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new RmlModuleReference(this, getText());
    }

    @Override
    public String toString() {
        return "ModuleName '" + getText() + "'";
    }
}
