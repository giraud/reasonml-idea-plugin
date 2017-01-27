package com.reason.psi;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.ReasonMLLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ReasonMLTokenType extends IElementType {
    public ReasonMLTokenType(@NotNull @NonNls String debugName) {
        super(debugName, ReasonMLLanguage.INSTANCE);
    }
}
