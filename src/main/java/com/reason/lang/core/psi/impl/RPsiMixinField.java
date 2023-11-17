package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiMixinField extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiMixinField(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
