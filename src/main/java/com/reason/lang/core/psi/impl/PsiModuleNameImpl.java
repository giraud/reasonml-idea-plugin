package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiModuleNameImpl extends MlASTWrapperPsiElement implements PsiModuleName {

    //region Constructors
    public PsiModuleNameImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
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
        return this; // Use PsiModuleReference.handleElementRename()
    }
    //endregion

    @Override
    public PsiReference getReference() {
        return new PsiModuleReference(this);
    }

    @Override
    public String toString() {
        return "Module.name(" + getName() + ")";
    }
}
