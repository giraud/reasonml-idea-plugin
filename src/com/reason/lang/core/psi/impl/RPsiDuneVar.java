package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class RPsiDuneVar extends ORCompositePsiElement<DuneTypes> {
    public RPsiDuneVar(@NotNull DuneTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
