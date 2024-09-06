package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiUnpack extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiUnpack(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiModuleSignature getSignature() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiModuleSignature.class);
    }

    public @Nullable RPsiUpperSymbol getModuleReference() {
        return ORUtil.findImmediateLastChildOfClass(getSignature(), RPsiUpperSymbol.class);
    }

    public @Nullable RPsiLowerSymbol getFirstClassSymbol() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiLowerSymbol.class);
    }
}
