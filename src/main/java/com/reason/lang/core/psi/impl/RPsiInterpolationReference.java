package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiInterpolationReference extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiInterpolationReference(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
