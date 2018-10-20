package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiTypeConstrName extends ASTWrapperPsiElement {

    //region Constructors
    public PsiTypeConstrName(@NotNull ASTNode node) {
        super(node);
    }
    //endregion


    @Override
    public String toString() {
        return "Type constr name";
    }
}
