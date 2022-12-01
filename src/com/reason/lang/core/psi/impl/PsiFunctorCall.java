package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class PsiFunctorCall extends ORCompositePsiElement<ORTypes> {
    protected PsiFunctorCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    private @Nullable PsiUpperSymbol getReferenceIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiUpperSymbol id = getReferenceIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        PsiUpperSymbol id = getReferenceIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @NotNull String getName() {
        PsiUpperSymbol name = getReferenceIdentifier();
        return name == null ? "" : name.getText();
    }

    public @NotNull Collection<PsiElement> getParameters() {
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
