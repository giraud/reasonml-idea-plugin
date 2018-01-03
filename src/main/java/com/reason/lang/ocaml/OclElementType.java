package com.reason.lang.ocaml;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class OclElementType extends IElementType {
    OclElementType(@NotNull @NonNls String debugName) {
        super(debugName, OclLanguage.INSTANCE);
    }
}
