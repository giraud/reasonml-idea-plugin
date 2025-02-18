package com.reason.lang.core.psi.impl;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiLowerName extends ORCompositePsiElement<ORLangTypes> implements PsiNamedElement, PsiNameIdentifierOwner {
    // region Constructors
    protected RPsiLowerName(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion


    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, RPsiLowerSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;

    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
        return null;
    }
}
