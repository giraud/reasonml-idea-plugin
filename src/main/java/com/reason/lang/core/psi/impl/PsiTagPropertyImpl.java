package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiTagProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTagPropertyImpl extends MlAstWrapperPsiElement implements PsiTagProperty {

    //region Constructors
    public PsiTagPropertyImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    @Nullable
    private PsiElement getNameElement() {
        return findChildByClass(PsiLowerSymbol.class);
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
