package com.reason.lang.core.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.ModuleName;

public class ModuleNameImpl extends ASTWrapperPsiElement implements ModuleName {
    public ModuleNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;  // TODO implement method
    }

    @Override
    public PsiElement getNameElement() {
        return findNotNullChildByType(RmlTypes.VALUE_NAME);
    }

    @Override
    public PsiReference getReference() {
        return new ModuleReference(this);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getNameElement();
    }
}
