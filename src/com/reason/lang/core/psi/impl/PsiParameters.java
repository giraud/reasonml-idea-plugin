package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiParameters extends ORCompositePsiElement<ORTypes> implements PsiElement {
    protected PsiParameters(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<PsiElement> getParametersList() {
        PsiElement parent = getParent();
        boolean isCall = parent instanceof PsiFunctionCall || parent instanceof PsiFunctorCall;
        return ORUtil.findImmediateChildrenOfType(this, isCall ? myTypes.C_PARAM : myTypes.C_PARAM_DECLARATION);
    }
}
