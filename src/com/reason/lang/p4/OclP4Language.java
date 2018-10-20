package com.reason.lang.p4;

import com.intellij.lang.Language;

public class OclP4Language extends Language {
    public static final OclP4Language INSTANCE = new OclP4Language();

    private OclP4Language() {
        super("OCamlP4");
    }
}
