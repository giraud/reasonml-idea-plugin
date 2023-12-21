package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.emptyList;

public class RPsiInherit extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiInherit(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiElement getClassTypeIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiLowerSymbol.class);
    }

    public @NotNull List<PsiElement> getParameters() {
        RPsiParameters parameters = ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class);
        return parameters != null ? parameters.getParametersList() : emptyList();
    }
}
