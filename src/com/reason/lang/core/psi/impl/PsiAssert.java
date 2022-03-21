package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import org.jetbrains.annotations.*;

public class PsiAssert extends ORCompositePsiElement {
    protected PsiAssert(IElementType type) {
        super(type);
    }

    public @Nullable PsiElement getAssertion() {
        return PsiTreeUtil.skipWhitespacesForward(getFirstChild());
    }
}
