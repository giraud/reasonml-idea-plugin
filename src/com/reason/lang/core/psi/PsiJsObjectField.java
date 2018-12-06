package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiJsObjectField extends ASTWrapperPsiElement {

    public PsiJsObjectField(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String toString() {
        return "JsObjectField";
    }
}
