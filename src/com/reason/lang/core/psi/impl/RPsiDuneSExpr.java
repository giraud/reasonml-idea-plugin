package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class RPsiDuneSExpr extends ORCompositePsiElement<DuneTypes> {
    public RPsiDuneSExpr(@NotNull DuneTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
