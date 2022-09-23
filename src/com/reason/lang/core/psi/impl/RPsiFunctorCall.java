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

public class RPsiFunctorCall extends ORCompositePsiElement<ORTypes> {
    protected RPsiFunctorCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    private @Nullable RPsiUpperSymbol getReferenceIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public @NotNull PsiElement getNavigationElement() {
        RPsiUpperSymbol id = getReferenceIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        RPsiUpperSymbol id = getReferenceIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @NotNull String getName() {
        RPsiUpperSymbol name = getReferenceIdentifier();
        return name == null ? "" : name.getText();
    }

    public @NotNull Collection<PsiElement> getParameters() {
        RPsiParameters params = PsiTreeUtil.findChildOfType(this, RPsiParameters.class);
        return params == null ? emptyList() : params.getParametersList();
    }

    public @Nullable RPsiFunctor resolveFunctor() {
        RPsiUpperSymbol uSymbol = ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
        PsiUpperSymbolReference reference = uSymbol == null ? null : (PsiUpperSymbolReference) uSymbol.getReference();
        PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();

        return resolvedElement instanceof RPsiFunctor ? (RPsiFunctor) resolvedElement : null;
    }
}
