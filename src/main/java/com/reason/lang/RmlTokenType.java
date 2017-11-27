package com.reason.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RmlTokenType extends IElementType {
    RmlTokenType(@NotNull @NonNls String debugName) {
        super(debugName, RmlLanguage.INSTANCE);
    }
}
