package com.reason.lang.core.type;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

public class ORCompositeElementType extends IElementType implements ORCompositeType {
    public ORCompositeElementType(@NotNull @NonNls String debugName, @NotNull Language language) {
        super(debugName, language);
    }
}
