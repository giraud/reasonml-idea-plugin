package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiFunction extends ORCompositePsiElement<ORTypes> implements PsiElement {
    protected RPsiFunction(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<RPsiParameterDeclaration> getParameters() {
        RPsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class);
        return ORUtil.findImmediateChildrenOfClass(parameters, RPsiParameterDeclaration.class);
    }

    public @Nullable RPsiFunctionBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctionBody.class);
    }
}
