package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiTagProperty extends ORCompositePsiElement<ORLangTypes> {
    // region Constructors
    protected RPsiTagProperty(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion

    @Nullable
    private PsiElement getNameElement() {
        return ORUtil.findImmediateFirstChildOfType(this, myTypes.PROPERTY_NAME);
    }

    @NotNull
    public String getName() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Nullable
    public PsiElement getValue() {
        PsiElement eq = ORUtil.nextSiblingWithTokenType(getFirstChild(), myTypes.EQ);
        return eq == null ? null : eq.getNextSibling();
    }
}
