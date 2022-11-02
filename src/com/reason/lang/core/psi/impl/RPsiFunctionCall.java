package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiFunctionCall extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiFunctionCall(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @NotNull String getName() {
        RPsiLowerSymbol name = ORUtil.findImmediateFirstChildOfClass(this, RPsiLowerSymbol.class);
        return name == null ? "" : name.getText();
    }

    public @NotNull List<RPsiParameterReference> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(
                ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class), RPsiParameterReference.class);
    }
}
