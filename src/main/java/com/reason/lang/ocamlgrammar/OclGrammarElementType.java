package com.reason.lang.ocamlgrammar;

import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class OclGrammarElementType extends IElementType {
    OclGrammarElementType(@NotNull String debugName) {
        super(debugName, OclGrammarLanguage.INSTANCE);
    }
}
