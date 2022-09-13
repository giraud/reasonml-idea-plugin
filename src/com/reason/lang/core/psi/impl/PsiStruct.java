package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiStruct extends ORCompositePsiElement<ORTypes> {
    protected PsiStruct(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
