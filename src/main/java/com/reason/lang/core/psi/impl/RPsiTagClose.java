package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiTagClose extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiTagClose(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
