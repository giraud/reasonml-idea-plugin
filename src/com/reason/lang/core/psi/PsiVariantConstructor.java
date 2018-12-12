package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiVariantConstructor extends ASTWrapperPsiElement {

    public PsiVariantConstructor(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiElement name = getFirstChild();
        return name == null ? "" : name.getText();
    }

    @NotNull
    @Override
    public String toString() {
        return "Variant constructor";
    }
}
