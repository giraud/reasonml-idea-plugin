package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiMixinField extends ASTWrapperPsiElement {

    public PsiMixinField(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }
}
