package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.PsiTypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTypeNameImpl extends ASTWrapperPsiElement implements PsiTypeName {
    public PsiTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByType(RmlTypes.VALUE_NAME);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiTypeReference.handleElementRename()
    }
    //endregion

    @Override
    public PsiReference getReference() {
        return new PsiTypeReference(this);
    }

    @Override
    public String toString() {
        return "Type.name(" + getName() + ")";
    }
}
