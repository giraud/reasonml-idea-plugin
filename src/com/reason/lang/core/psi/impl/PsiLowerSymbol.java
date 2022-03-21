package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiLowerSymbol extends CompositeTypePsiElement<ORTypes> implements PsiElement {
    // region Constructors
    protected PsiLowerSymbol(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion

    @Override
    public PsiReference getReference() {
        return new PsiLowerSymbolReference(this, m_types);
    }
}
