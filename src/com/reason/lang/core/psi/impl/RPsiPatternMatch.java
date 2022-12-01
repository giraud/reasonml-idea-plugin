package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiPatternMatch extends ORCompositePsiElement<ORTypes> {
    protected RPsiPatternMatch(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiPatternMatchBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiPatternMatchBody.class);
    }
}
