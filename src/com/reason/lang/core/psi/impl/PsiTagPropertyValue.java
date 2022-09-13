package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiTagPropertyValue extends ORCompositePsiElement<ORTypes> {
    // region Constructors
    protected PsiTagPropertyValue(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
    // endregion


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
