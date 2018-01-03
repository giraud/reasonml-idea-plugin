package com.reason.lang.reason;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.RmlLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class RmlElementType extends IElementType {
    RmlElementType(@NotNull @NonNls String debugName) {
        super(debugName, RmlLanguage.INSTANCE);
    }
}
