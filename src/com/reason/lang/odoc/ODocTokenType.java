package com.reason.lang.odoc;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class ODocTokenType extends IElementType {
    public ODocTokenType(@NotNull String debugName) {
        super(debugName, ODocLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ODocTokenType." + super.toString();
    }
}
