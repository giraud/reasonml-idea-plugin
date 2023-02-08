package com.reason.lang.reason;

import com.intellij.lexer.*;

public class RmlLexer extends FlexAdapter {
    public RmlLexer() {
        super(new ReasonMLLexer(RmlTypes.INSTANCE));
    }
}
