package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiTernary extends CompositeTypePsiElement<ORTypes> implements PsiConditional {
    protected PsiTernary(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable PsiBinaryCondition getCondition() {
        return findChildByClass(PsiBinaryCondition.class);
    }

    @Override
    public @Nullable PsiElement getThenExpression() {
        PsiElement element = ORUtil.findImmediateFirstChildOfType(this, m_types.QUESTION_MARK);
        return element == null ? null : ORUtil.nextSibling(element);
    }

    @Override
    public @Nullable PsiElement getElseExpression() {
        PsiElement element = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
        return element == null ? null : ORUtil.nextSibling(element);
    }
}
