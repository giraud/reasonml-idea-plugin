package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiPolyVariantConstraint extends ORCompositePsiElement<ORTypes> {
    protected PsiPolyVariantConstraint(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public boolean isOpen() {
        PsiElement bracket = ORUtil.findImmediateFirstChildOfType(this, myTypes.LBRACKET);
        PsiElement open = bracket == null ? null : bracket.getNextSibling();
        return open != null && open.getNode().getElementType() == myTypes.GT;
    }
}
