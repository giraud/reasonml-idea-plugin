package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import com.reason.lang.dune.*;
import org.jetbrains.annotations.*;

public class RPsiDuneFields extends ORCompositePsiElement<DuneTypes> {
    public RPsiDuneFields(@NotNull DuneTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
