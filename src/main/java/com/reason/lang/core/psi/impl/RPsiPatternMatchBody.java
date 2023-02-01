package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiPatternMatchBody extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiPatternMatchBody(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
