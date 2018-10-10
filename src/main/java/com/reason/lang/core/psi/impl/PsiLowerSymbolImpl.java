package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.RmlElementFactory;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.reference.PsiLowerSymbolReference;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLowerSymbolImpl extends PsiToken<ORTypes> implements PsiLowerSymbol {

    //region Constructors
    public PsiLowerSymbolImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
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
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createLetName(getProject(), newName);

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
    public PsiReference getReference() {
        return new PsiLowerSymbolReference(this, m_types);
    }

    @Override
    public String toString() {
        return "LSymbol " + getName();
    }
}
