package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiTryHandler extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiTryHandler(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiTryHandlerBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiTryHandlerBody.class);
    }
}
