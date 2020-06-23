package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiMacro extends ASTWrapperPsiElement {

    public PsiMacro(@NotNull ASTNode node) {
        super(node);
    }

}
