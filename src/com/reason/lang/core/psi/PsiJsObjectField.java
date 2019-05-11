package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiJsObjectField extends ASTWrapperPsiElement {

    public PsiJsObjectField(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String toString() {
        return "JsObjectField";
    }

    @Nullable
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }
}
