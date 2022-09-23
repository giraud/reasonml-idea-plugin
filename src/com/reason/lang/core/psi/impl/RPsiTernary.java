package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiTernary extends ORCompositePsiElement<ORTypes> implements RPsiConditional {
    protected RPsiTernary(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable RPsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiBinaryCondition.class);
    }

    @Override
    public @Nullable PsiElement getThenExpression() {
        PsiElement element = ORUtil.findImmediateFirstChildOfType(this, myTypes.QUESTION_MARK);
        return element == null ? null : ORUtil.nextSibling(element);
    }

    @Override
    public @Nullable PsiElement getElseExpression() {
        PsiElement element = ORUtil.findImmediateFirstChildOfType(this, myTypes.COLON);
        return element == null ? null : ORUtil.nextSibling(element);
    }
}
