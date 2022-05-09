package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiTry extends ORCompositeTypePsiElement<ORTypes> {
    protected PsiTry(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_BODY);
    }

    public @Nullable List<PsiElement> getHandlers() {
        PsiElement scopedElement = ORUtil.findImmediateFirstChildOfType(this, (IElementType) m_types.C_TRY_HANDLERS);
        return ORUtil.findImmediateChildrenOfType(scopedElement, (IElementType) m_types.C_TRY_HANDLER);
    }
}
