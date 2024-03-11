package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.type.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public class RPsiLiteralString extends LeafPsiElement {
    private final ORLangTypes myTypes;

    public RPsiLiteralString(@NotNull ORLangTypes types, @NotNull IElementType type, CharSequence text) {
        super(type, text);
        myTypes = types;
    }

    @Override
    public @Nullable PsiReference getReference() {
        return myTypes instanceof ResTypes ? new ORPsiLiteralStringReference(this, myTypes) : null;
    }

    @Override
    public @NotNull String toString() {
        return "RPsiLiteralString";
    }
}
