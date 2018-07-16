package com.reason.lang.ocaml;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.tree.IElementType;

class OclElementType extends IElementType {
    OclElementType(@NotNull @NonNls String debugName) {
        super(debugName, OclLanguage.INSTANCE);
    }
}
