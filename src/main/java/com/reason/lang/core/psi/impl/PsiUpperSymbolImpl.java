package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.reference.PsiUpperSymbolReference;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiUpperSymbolImpl extends MlAstWrapperPsiElement implements PsiUpperSymbol {

    //region Constructors
    public PsiUpperSymbolImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
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

    @Override
    public boolean isVariant() {
        PsiElement firstChild = getFirstChild();
        return firstChild != null && firstChild.getNode().getElementType() == m_types.VARIANT_NAME;
    }

    @Override
    public PsiReference getReference() {
        return new PsiUpperSymbolReference(this, m_types);
    }

    @Override
    public String toString() {
        String name = getName();
        return "USymbol " + (name == null || name.isEmpty() ? "<" + ((FileBase) getContainingFile()).asModuleName() + ">" : name);
    }
}
