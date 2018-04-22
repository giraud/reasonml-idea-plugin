package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class DuneElementType extends IElementType {
    DuneElementType(@NotNull @NonNls String debugName) {
        super(debugName, DuneLanguage.INSTANCE);
    }

}
