package com.reason.lang;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.core.psi.type.MlTypes;

public class LexerAdapter extends FlexAdapter {
    public LexerAdapter(MlTypes types) {
        super(new ReasonMLLexer(types));
    }
}
