package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

/**
 * Module conversion to a value of the core language that encapsulates this module.
 */
public class RPsiModuleValue extends ORCompositePsiElement<ORLangTypes> {
    public RPsiModuleValue(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
