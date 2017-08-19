package com.reason.lang;

import com.intellij.lexer.FlexAdapter;

public class ReasonMLLexerAdapter extends FlexAdapter {
    public ReasonMLLexerAdapter() {
        super(new ReasonMLLexer(null));
    }
}
