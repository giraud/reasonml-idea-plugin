package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiFunctorResult extends CompositeTypePsiElement<ORTypes> {
    protected PsiFunctorResult(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiUpperSymbol getModuleType() {
        return ORUtil.findImmediateLastChildOfClass(this, PsiUpperSymbol.class);
    }

    public @Nullable PsiElement resolveModule() {
        PsiUpperSymbol moduleType = getModuleType();
        PsiUpperSymbolReference reference = moduleType == null ? null : (PsiUpperSymbolReference) moduleType.getReference();
        PsiElement resolvedSymbol = reference == null ? null : reference.resolveInterface();
        if (resolvedSymbol instanceof PsiUpperIdentifier) {
            return resolvedSymbol.getParent();
        } else if (resolvedSymbol instanceof PsiFakeModule) {
            return resolvedSymbol.getContainingFile();
        }

        return null;
    }
}
