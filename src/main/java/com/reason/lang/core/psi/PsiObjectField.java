package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class PsiObjectField extends ASTWrapperPsiElement {

    public PsiObjectField(ASTNode node) {
        super(node);
    }

    @Nullable
    private PsiElement getNameElement() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public String toString() {
        return "Object field " + getName();
    }
}