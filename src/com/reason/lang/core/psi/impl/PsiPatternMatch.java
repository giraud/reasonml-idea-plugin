package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import org.jetbrains.annotations.*;

public class PsiPatternMatch extends ORCompositePsiElement {
    protected PsiPatternMatch(IElementType type) {
        super(type);
    }

    public @Nullable PsiPatternMatchBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiPatternMatchBody.class);
    }
}
