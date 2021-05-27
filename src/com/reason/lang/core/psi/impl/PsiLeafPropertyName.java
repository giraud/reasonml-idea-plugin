package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.reference.*;
import org.jetbrains.annotations.*;

public class PsiLeafPropertyName extends LeafPsiElement {
    public PsiLeafPropertyName(@NotNull IElementType type, CharSequence text) {
        super(type, text);
    }

    @Override
    public PsiReference getReference() {
        return new PsiPropertyNameReference(this, ORUtil.getTypes(getLanguage()));
    }

    @Override
    public String toString() {
        return "PropertyName:" + getText();
    }
}
