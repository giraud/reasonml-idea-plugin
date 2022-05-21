package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiUpperTagName extends PsiUpperSymbol {
    public PsiUpperTagName(@NotNull ORTypes types, @NotNull IElementType tokenType, CharSequence text) {
        super(types, tokenType, text);
    }

    @Override
    public @NotNull String toString() {
        return "PsiUpperTagName:" + getText();
    }
}
