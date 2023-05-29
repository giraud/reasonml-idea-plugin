package com.reason.lang.ocamllex;

import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class OclLexElementType extends IElementType {
    public OclLexElementType(@NotNull String debugName) {
        super(debugName, OclLexLanguage.INSTANCE);
    }
}
