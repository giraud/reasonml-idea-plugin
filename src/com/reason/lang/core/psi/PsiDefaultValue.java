package com.reason.lang.core.psi;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class PsiDefaultValue extends CompositePsiElement {
    public PsiDefaultValue(@NotNull IElementType elementType) {
        super(elementType);
    }

    @Override
    public @NotNull String toString() {
        return "Default value";
    }
}