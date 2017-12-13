package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTagProperty extends ASTWrapperPsiElement {
    public PsiTagProperty(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiElement getNameElement() {
        return findChildByType(RmlTypes.PROPERTY_NAME);
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Override
    public String toString() {
        return "TagProperty(" + getName() + ")";
    }
}
