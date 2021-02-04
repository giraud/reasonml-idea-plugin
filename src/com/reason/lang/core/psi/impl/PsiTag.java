package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiTag extends CompositeTypePsiElement<ORTypes> {
    protected PsiTag(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable String getName() {
        PsiTagStart tagStart = ORUtil.findImmediateFirstChildOfClass(this, PsiTagStart.class);
        return tagStart == null ? null : ORUtil.getTextUntilWhitespace(tagStart.getFirstChild().getNextSibling());
    }

    @NotNull
    public Collection<PsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(getFirstChild() /*tag start*/, PsiTagProperty.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Tag";
    }
}
