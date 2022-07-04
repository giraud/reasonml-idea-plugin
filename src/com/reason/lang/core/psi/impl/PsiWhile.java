package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiWhile extends ORCompositePsiElement<ORTypes> {
    protected PsiWhile(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiBinaryCondition.class);
    }

    public @Nullable PsiScopedExpr getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
    }
}
