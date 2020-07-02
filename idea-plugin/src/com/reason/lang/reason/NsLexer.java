package com.reason.lang.reason;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.ReasonMLLexer;

public class NsLexer extends FlexAdapter {
    public NsLexer() {
        super(new ReasonMLLexer(NsTypes.INSTANCE));
    }
}
