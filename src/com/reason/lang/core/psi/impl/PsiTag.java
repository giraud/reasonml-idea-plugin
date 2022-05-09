package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiTag extends ORCompositeTypePsiElement<ORTypes> {
    protected PsiTag(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable String getName() {
        PsiTagStart tagStart = ORUtil.findImmediateFirstChildOfClass(this, PsiTagStart.class);
        return tagStart == null ? null : ORUtil.getTextUntilWhitespace(tagStart.getFirstChild().getNextSibling());
    }

    public @NotNull Collection<PsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(getFirstChild(/*tag_start*/), PsiTagProperty.class);
    }

    public @Nullable PsiTagBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiTagBody.class);
    }
}
