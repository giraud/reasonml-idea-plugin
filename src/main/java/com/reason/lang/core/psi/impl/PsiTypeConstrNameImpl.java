package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.reference.PsiTypeConstrNameReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTypeConstrNameImpl extends MlAstWrapperPsiElement implements PsiTypeConstrName {

    //region Constructors
    public PsiTypeConstrNameImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? PsiUtil.fileNameToModuleName(getContainingFile()) : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiUpperSymbolReference.handleElementRename()
    }
    //endregion

    @Nullable
    @Override
    public String getQualifiedName() {
        return null;
    }

    @Override
    public PsiReference getReference() {
        return new PsiTypeConstrNameReference(this);
    }

}
