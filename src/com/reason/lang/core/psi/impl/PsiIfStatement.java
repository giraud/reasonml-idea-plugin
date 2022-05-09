package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiIfStatement extends ORCompositeTypePsiElement<ORTypes> implements PsiConditional {
    protected PsiIfStatement(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable
    public PsiBinaryCondition getCondition() {
        return findChildByClass(PsiBinaryCondition.class);
    }

    @Override
    public @Nullable PsiElement getThenExpression() {
        PsiBinaryCondition condition = getCondition();
        return condition == null ? null : ORUtil.nextSibling(condition);
    }

    @Override
    public @Nullable PsiElement getElseExpression() {
        PsiElement else_ = ORUtil.findImmediateFirstChildOfType(this, m_types.ELSE);
        return else_ == null ? null : ORUtil.nextSibling(else_);
    }
}
