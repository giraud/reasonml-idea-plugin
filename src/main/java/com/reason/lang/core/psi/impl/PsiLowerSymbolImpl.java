package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.reference.PsiLowerSymbolReference;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLowerSymbolImpl extends MlAstWrapperPsiElement implements PsiLowerSymbol {

    //region Constructors
    public PsiLowerSymbolImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    public PsiReference getReference() {
        return new PsiLowerSymbolReference(this, m_types);
    }

    @Override
    public String toString() {
        return "LSymbol " + getName();
    }
}
