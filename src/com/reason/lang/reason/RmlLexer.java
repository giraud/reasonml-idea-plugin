package com.reason.lang.reason;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.ReasonMLLexer;

public class RmlLexer extends FlexAdapter {
    public RmlLexer() {
        super(new ReasonMLLexer(RmlTypes.INSTANCE));
    }
}
