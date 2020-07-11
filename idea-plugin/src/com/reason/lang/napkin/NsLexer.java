package com.reason.lang.napkin;

import com.intellij.lexer.FlexAdapter;
import com.reason.lang.NapkinLexer;

public class NsLexer extends FlexAdapter {
    public NsLexer() {
        super(new NapkinLexer(NsTypes.INSTANCE));
    }
}
