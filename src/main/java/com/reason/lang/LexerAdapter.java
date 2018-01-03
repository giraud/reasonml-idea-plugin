package com.reason.lang;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.ReasonMLLexer;
import com.reason.lang.reason.RmlTypes;

public class LexerAdapter extends FlexAdapter {
    public LexerAdapter(MlTypes types) {
        super(new ReasonMLLexer(types));
    }
}
