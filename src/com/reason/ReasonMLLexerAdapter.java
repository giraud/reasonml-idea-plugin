package com.reason;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class ReasonMLLexerAdapter extends FlexAdapter {
    public ReasonMLLexerAdapter() {
        super(new ReasonMLLexer((Reader) null));
    }
}
