package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiFunction extends ORCompositePsiElement<ORTypes> implements PsiElement {
    protected PsiFunction(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<PsiParameterDeclaration> getParameters() {
        PsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class);
        return ORUtil.findImmediateChildrenOfClass(parameters, PsiParameterDeclaration.class);
    }

    public @Nullable PsiFunctionBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctionBody.class);
    }
}
