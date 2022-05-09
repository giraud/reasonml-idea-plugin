package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class PsiLeafTagName extends LeafPsiElement {
    public PsiLeafTagName(@NotNull IElementType type, CharSequence text) {
        super(type, text);
    }

    @Override
    public @NotNull String toString() {
        return "ORLeaf(TAG_NAME)";
    }
}
