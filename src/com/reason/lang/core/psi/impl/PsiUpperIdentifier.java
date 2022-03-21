package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiUpperIdentifier extends CompositeTypePsiElement<ORTypes> implements PsiNameIdentifierOwner {
    // region Constructors
    protected PsiUpperIdentifier(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion

    // region NamedElement
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        PsiUpperIdentifier newNameIdentifier = ORCodeFactory.createModuleName(getProject(), newName);

        ASTNode newNameNode =
                newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();
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
    // endregion
}
