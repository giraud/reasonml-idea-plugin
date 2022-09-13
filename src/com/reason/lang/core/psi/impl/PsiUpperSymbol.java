package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiUpperSymbol extends LeafPsiElement implements PsiORAtom {
    protected final ORTypes myTypes;

    // region Constructors
    public PsiUpperSymbol(@NotNull ORTypes types, @NotNull IElementType tokenType, CharSequence text) {
        super(tokenType, text);
        myTypes = types;
    }
    // endregion

    @Override
    public @NotNull PsiReference getReference() {
        return new PsiUpperSymbolReference(this, myTypes);
    }

    @Override public String toString() {
        return "PsiUpperSymbol:" + getElementType();
    }
}
