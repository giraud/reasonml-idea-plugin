package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiTry extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiTry(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiTryBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiTryBody.class);
    }

    public @Nullable List<RPsiTryHandler> getHandlers() {
        PsiElement scopedElement = ORUtil.findImmediateFirstChildOfType(this, (IElementType) myTypes.C_TRY_HANDLERS);
        return ORUtil.findImmediateChildrenOfClass(scopedElement, RPsiTryHandler.class);
    }
}
