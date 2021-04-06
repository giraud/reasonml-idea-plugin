package com.reason.lang.ocaml;

import com.intellij.lexer.*;

public class OclLexer extends FlexAdapter {
    public OclLexer() {
        super(new OCamlLexer());
    }
}
