package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiPatternMatch extends ORCompositePsiElement<ORTypes> {
    protected PsiPatternMatch(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiPatternMatchBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiPatternMatchBody.class);
    }
}
