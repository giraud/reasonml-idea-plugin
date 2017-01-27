package com.reason.lang;

import com.intellij.lexer.FlexAdapter;
import com.reason.ReasonMLLexer;

import java.io.Reader;

public class ReasonMLLexerAdapter extends FlexAdapter {
    public ReasonMLLexerAdapter() {
        super(new ReasonMLLexer((Reader) null));
    }
}
