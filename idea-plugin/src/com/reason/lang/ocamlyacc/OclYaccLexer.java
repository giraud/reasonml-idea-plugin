package com.reason.lang.ocamlyacc;

import com.intellij.lexer.FlexAdapter;

class OclYaccLexer extends FlexAdapter {
    OclYaccLexer() {
        super(new YaccLexer());
    }
}
