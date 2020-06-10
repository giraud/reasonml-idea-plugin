package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiTag extends ASTWrapperPsiElement {
    public PsiTag(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public Collection<PsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(getFirstChild()/*tag start*/, PsiTagProperty.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Tag";
    }
}
