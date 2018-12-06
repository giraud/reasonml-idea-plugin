package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiJsObject extends ASTWrapperPsiElement {

    public PsiJsObject(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    public Collection<PsiJsObjectField> getFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiJsObjectField.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "JsObject";
    }

}
