package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiAnnotationImpl extends ORCompositeTypePsiElement<ORTypes> implements PsiAnnotation {
    protected PsiAnnotationImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(PsiMacroName.class);
    }

    @Override
    public @Nullable String getName() {
        PsiElement identifier = getNameIdentifier();
        return identifier == null ? null : identifier.getText();
    }

    @Override
    public @Nullable PsiElement getValue() {
        PsiScopedExpr expr = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
        if (expr != null) {
            return ORUtil.nextSibling(expr.getFirstChild());
        }
        return null;
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
