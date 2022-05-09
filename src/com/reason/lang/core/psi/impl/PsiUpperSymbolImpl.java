package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiUpperSymbolImpl extends ORCompositeTypePsiElement<ORTypes> implements PsiUpperSymbol {
    // region Constructors
    protected PsiUpperSymbolImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion

    @Override
    public PsiReference getReference() {
        return new PsiUpperSymbolReference(this, m_types);
    }
}
