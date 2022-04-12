package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorBinding extends ORCompositePsiElement {
    protected PsiFunctorBinding(@NotNull IElementType elementType) {
        super(elementType);
    }
}
