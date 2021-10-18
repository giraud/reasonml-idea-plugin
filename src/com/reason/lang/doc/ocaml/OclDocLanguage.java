package com.reason.lang.doc.ocaml;

import com.intellij.lang.Language;

public class OclDocLanguage extends Language {
    public static final OclDocLanguage INSTANCE = new OclDocLanguage();

    private OclDocLanguage() {
        super("ODoc");
    }
}
