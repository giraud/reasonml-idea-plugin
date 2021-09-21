package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class PsiMacroName extends CompositePsiElement {
    protected PsiMacroName(IElementType type) {
        super(type);
    }

    @Override
    public @NotNull String toString() {
        return "Macro name (" + getText() + ")";
    }
}
