package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiTag extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiTag(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @Nullable String getName() {
        RPsiTagStart tagStart = ORUtil.findImmediateFirstChildOfClass(this, RPsiTagStart.class);
        return tagStart == null ? null : ORUtil.getLongIdent(tagStart.getFirstChild().getNextSibling());
    }

    public @NotNull List<RPsiTagProperty> getProperties() {
        return ORUtil.findImmediateChildrenOfClass(getFirstChild(/*tag_start*/), RPsiTagProperty.class);
    }

    public @Nullable RPsiTagBody getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiTagBody.class);
    }
}
