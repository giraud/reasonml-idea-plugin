package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.RmlElementFactory;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.reference.PsiUpperSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiUpperSymbolImpl extends MlAstWrapperPsiElement implements PsiUpperSymbol {

    //region Constructors
    public PsiUpperSymbolImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? ORUtil.fileNameToModuleName(getContainingFile()) : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(getProject(), newName);

        ASTNode newNameNode = newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();
        if (newNameNode != null) {
            PsiElement nameIdentifier = getFirstChild();
            if (nameIdentifier == null) {
                getNode().addChild(newNameNode);
            } else {
                ASTNode oldNameNode = nameIdentifier.getNode();
                getNode().replaceChild(oldNameNode, newNameNode);
            }
        }

        return this;
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
