package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiFunctionCall extends CompositeTypePsiElement<ORTypes> {
    protected PsiFunctionCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public
    @NotNull List<PsiParameter> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(
                ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
    }
}
