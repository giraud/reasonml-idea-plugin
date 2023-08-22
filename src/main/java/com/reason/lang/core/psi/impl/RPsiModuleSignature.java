package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

/*
 module M : Signature = ...
 module M : { /...signature/ } = ...
 */
public class RPsiModuleSignature extends ORCompositePsiElement<ORLangTypes> implements RPsiQualifiedPathElement {
    protected RPsiModuleSignature(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public String @Nullable [] getPath() {
        return ORUtil.getQualifiedPath(this);

    }

    public @Nullable RPsiUpperSymbol getNameIdentifier() {
        return ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    @Override
    public @Nullable String getQualifiedName() {
        return ORUtil.getQualifiedName(this);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
