package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiClassConstructor extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiClassConstructor(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
