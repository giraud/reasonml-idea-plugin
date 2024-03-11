package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class RPsiFirstClass extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiFirstClass(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiUpperSymbol getFirstClassModuleSymbol() {
        if (myTypes == OclTypes.INSTANCE) {
            return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
        }

        RPsiScopedExpr scope = ORUtil.findImmediateLastChildOfClass(this, RPsiScopedExpr.class);
        return scope != null ? ORUtil.findImmediateLastChildOfClass(scope, RPsiUpperSymbol.class) : null;
    }
}
