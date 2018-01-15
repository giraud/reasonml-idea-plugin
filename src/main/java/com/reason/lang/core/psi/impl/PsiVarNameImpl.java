package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiVarName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiVarNameImpl extends MlAstWrapperPsiElement implements PsiVarName {

    //region Constructors
    public PsiVarNameImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
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
        return findChildByType(m_types.VALUE_NAME);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiTypeReference.handleElementRename()
    }
    //endregion

    @Override
    public String toString() {
        return "Name(" + getName() + ")";
    }
}
