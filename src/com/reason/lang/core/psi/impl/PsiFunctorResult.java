package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiFunctorResult extends ORCompositePsiElement<ORTypes> {
    protected PsiFunctorResult(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiUpperSymbol getModuleType() {
        return ORUtil.findImmediateLastChildOfClass(this, PsiUpperSymbol.class);
    }

    public @Nullable PsiElement resolveModule() {
        PsiUpperSymbol moduleType = getModuleType();
        PsiUpperSymbolReference reference = moduleType == null ? null : (PsiUpperSymbolReference) moduleType.getReference();
        PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
        return resolvedElement instanceof PsiFakeModule ? resolvedElement.getContainingFile() : resolvedElement;
    }
}
