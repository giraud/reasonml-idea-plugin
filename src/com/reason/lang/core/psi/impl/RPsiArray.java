package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiArray extends ORCompositePsiElement<ORTypes> {
    protected RPsiArray(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
