package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class PsiFunctorCall extends ORCompositePsiElement<ORTypes> {
    protected PsiFunctorCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @NotNull
    public String getFunctorName() {
        String text = getText();

        PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
        if (params == null) {
            return text;
        }

        return text.substring(0, params.getTextOffset() - getTextOffset());
    }

    public @NotNull Collection<PsiParameter> getParameters() {
        PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
        return params == null ? emptyList() : params.getParametersList();
    }

    public @Nullable PsiFunctor resolveFunctor() {
        PsiUpperSymbol uSymbol = ORUtil.findImmediateLastChildOfClass(this, PsiUpperSymbol.class);
        PsiUpperSymbolReference reference = uSymbol == null ? null : (PsiUpperSymbolReference) uSymbol.getReference();
        PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();

        return resolvedElement instanceof PsiFunctor ? (PsiFunctor) resolvedElement : null;
    }
}
