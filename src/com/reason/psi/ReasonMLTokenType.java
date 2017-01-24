package com.reason.psi;

import com.intellij.psi.tree.IElementType;
import com.reason.ReasonMLLanguage;
import org.jetbrains.annotations.*;

public class ReasonMLTokenType extends IElementType {
    public ReasonMLTokenType(@NotNull @NonNls String debugName) {
        super(debugName, ReasonMLLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "ReasonMLTokenType." + super.toString();
    }
}
