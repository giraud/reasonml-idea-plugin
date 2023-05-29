package com.reason.lang.ocamllex;

import com.intellij.lang.*;

public class OclLexLanguage extends Language {
    public static final OclLexLanguage INSTANCE = new OclLexLanguage();

    private OclLexLanguage() {
        super("Mll");
    }
}
