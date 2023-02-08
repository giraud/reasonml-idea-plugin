package com.reason.lang.core.type;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class ORCompositeElementType extends IElementType implements ORCompositeType {
    public ORCompositeElementType(@NotNull @NonNls String debugName, @NotNull Language language) {
        super(debugName, language);
    }
}
