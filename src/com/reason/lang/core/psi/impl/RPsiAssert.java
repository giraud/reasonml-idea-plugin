package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiAssert extends ORCompositePsiElement<ORLangTypes> {

    protected RPsiAssert(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiElement getAssertion() {
        return PsiTreeUtil.skipWhitespacesForward(getFirstChild());
    }
}
