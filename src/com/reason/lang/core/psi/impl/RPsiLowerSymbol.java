package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiLowerSymbol extends LeafPsiElement implements RPsiAtom {
    protected final ORTypes myTypes;

    // region Constructors
    public RPsiLowerSymbol(@NotNull ORTypes types, @NotNull IElementType tokenType, CharSequence text) {
        super(tokenType, text);
        myTypes = types;
    }
    // endregion

    @Override
    public PsiReference getReference() {
        return new PsiLowerSymbolReference(this, myTypes);
    }

    @Override public String toString() {
        return "RPsiLowerSymbol:" + getElementType();
    }
}
