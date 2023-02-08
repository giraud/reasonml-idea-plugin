package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiParameterReference extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiParameterReference(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override public String getName() {
        PsiElement nameIdentifier = null;

        PsiElement firstChild = getFirstChild();
        if (firstChild != null && firstChild.getNode().getElementType() == myTypes.TILDE) {
            nameIdentifier = ORUtil.nextSiblingWithTokenType(firstChild, myTypes.LIDENT);
        }

        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    public @Nullable PsiElement getValue() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiDefaultValue.class);
    }
}
