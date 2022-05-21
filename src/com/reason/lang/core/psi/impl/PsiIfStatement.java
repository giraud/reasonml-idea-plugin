package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiIfStatement extends ORCompositePsiElement<ORTypes> implements PsiConditional {
    protected PsiIfStatement(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable
    public PsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiBinaryCondition.class);
    }

    @Override
    public @Nullable PsiElement getThenExpression() {
        return ORUtil.findImmediateFirstChildOfType(this, myTypes.C_IF_THEN_SCOPE);
    }

    @Override
    public @Nullable PsiElement getElseExpression() {
        return ORUtil.findImmediateLastChildOfType(this, myTypes.C_IF_THEN_SCOPE);
    }
}
