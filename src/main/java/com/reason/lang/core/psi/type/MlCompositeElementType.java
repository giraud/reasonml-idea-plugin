package com.reason.lang.core.psi.type;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MlCompositeElementType extends IElementType {
    public MlCompositeElementType(@NotNull @NonNls String debugName, @NotNull Language language) {
        super(debugName, language);
    }
}
