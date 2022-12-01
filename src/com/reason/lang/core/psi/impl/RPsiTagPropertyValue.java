package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiTagPropertyValue extends ORCompositePsiElement<ORLangTypes> {
    // region Constructors
    protected RPsiTagPropertyValue(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
