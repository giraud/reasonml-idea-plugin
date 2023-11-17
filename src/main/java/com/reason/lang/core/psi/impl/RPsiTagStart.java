package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiTagStart extends ORCompositePsiElement<ORLangTypes> implements PsiNameIdentifierOwner {
    protected RPsiTagStart(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement lastTag = null;

        PsiElement element = getFirstChild();
        IElementType elementType = element == null ? null : element.getNode().getElementType();
        while (elementType == myTypes.A_UPPER_TAG_NAME || elementType == myTypes.A_LOWER_TAG_NAME || elementType == myTypes.DOT || elementType == myTypes.LT) {
            if (elementType != myTypes.DOT && elementType != myTypes.LT) {
                lastTag = element;
            }
            element = element.getNextSibling();
            elementType = element == null ? null : element.getNode().getElementType();
        }

        return lastTag;
    }

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    public @NotNull List<RPsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(this, RPsiTagProperty.class);
    }

    @Override
    public String toString() {
        return "RPsiTagStart: " + getName();
    }
}
