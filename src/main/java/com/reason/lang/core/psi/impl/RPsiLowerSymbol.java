package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiLowerSymbol extends LeafPsiElement {
    protected final ORLangTypes myTypes;

    // region Constructors
    public RPsiLowerSymbol(@NotNull ORLangTypes types, @NotNull IElementType tokenType, @NotNull CharSequence text) {
        super(tokenType, text);
        myTypes = types;
    }
    // endregion

    @Override
    public @NotNull RPsiLowerSymbolReference getReference() {
        return new RPsiLowerSymbolReference(this, myTypes);
    }

    @Override
    public @NotNull String toString() {
        return "RPsiLowerSymbol:" + getElementType() + " (" + getText() + ")";
    }
}
