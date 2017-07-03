package com.reason.lang;

import com.intellij.lexer.FlexAdapter;
import com.reason.ReasonMLLexer;

public class ReasonMLLexerAdapter extends FlexAdapter {
    public ReasonMLLexerAdapter() {
        super(new ReasonMLLexer(null));
    }
}
