package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiLetAttribute extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiLetAttribute(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable String getValue() {
        PsiElement nextSibling = getFirstChild().getNextSibling();
        return nextSibling == null ? null : nextSibling.getText();
    }
}
