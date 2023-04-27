package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiFunctorResult extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiFunctorResult(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiUpperSymbol getModuleType() {
        return ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
    }
}
