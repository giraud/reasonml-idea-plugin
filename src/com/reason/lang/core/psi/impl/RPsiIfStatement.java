package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiIfStatement extends ORCompositePsiElement<ORLangTypes> implements RPsiConditional {
    protected RPsiIfStatement(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable
    public RPsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiBinaryCondition.class);
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
