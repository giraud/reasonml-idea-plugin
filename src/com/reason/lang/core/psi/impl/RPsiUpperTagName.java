package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiUpperTagName extends RPsiUpperSymbol {
    public RPsiUpperTagName(@NotNull ORTypes types, @NotNull IElementType tokenType, CharSequence text) {
        super(types, tokenType, text);
    }

    @Override
    public @NotNull String toString() {
        return "RPsiUpperTagName:" + getText();
    }
}
