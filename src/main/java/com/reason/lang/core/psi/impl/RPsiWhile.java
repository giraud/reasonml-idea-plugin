package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiWhile extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiWhile(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiBinaryCondition.class);
    }

    public @Nullable RPsiScopedExpr getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiScopedExpr.class);
    }
}
