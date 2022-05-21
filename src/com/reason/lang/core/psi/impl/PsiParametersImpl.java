package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiParametersImpl extends ORCompositePsiElement<ORTypes> implements PsiParameters {
    protected PsiParametersImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @NotNull List<PsiParameter> getParametersList() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiParameter.class);
    }
}
