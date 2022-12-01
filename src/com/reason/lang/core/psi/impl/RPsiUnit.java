package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class RPsiUnit extends CompositePsiElement {
    protected RPsiUnit(IElementType type) {
        super(type);
    }

    @Override
    public @NotNull String toString() {
        return "Unit";
    }
}
