package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiScopedExpr extends ORCompositePsiElement<ORTypes> {
    protected PsiScopedExpr(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public boolean isEmpty() {
        PsiElement firstChild = getFirstChild();
        IElementType firstType = firstChild == null ? null : firstChild.getNode().getElementType();
        if (firstType == myTypes.LPAREN) {
            assert firstChild != null;
            PsiElement secondChild = firstChild.getNextSibling();
            IElementType secondType = secondChild == null ? null : secondChild.getNode().getElementType();
            return secondType == myTypes.RPAREN;
        }

        return false;
    }
}
