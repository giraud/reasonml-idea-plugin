package com.reason.lang;

import com.intellij.lexer.FlexAdapter;

public class RmlLexerAdapter extends FlexAdapter {
    public RmlLexerAdapter() {
        super(new ReasonMLLexer(null));
    }
}
