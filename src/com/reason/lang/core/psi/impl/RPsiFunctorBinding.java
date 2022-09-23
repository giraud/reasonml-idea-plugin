package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.NotNull;

public class RPsiFunctorBinding extends ORCompositePsiElement<ORTypes> {
    protected RPsiFunctorBinding(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
