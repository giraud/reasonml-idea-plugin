package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiGuard extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiGuard(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable
    public RPsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiBinaryCondition.class);
    }
}
