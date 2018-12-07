package com.reason.lang.extra;

import com.intellij.lang.Language;

public class OclMlyLanguage extends Language {
    public static final OclMlyLanguage INSTANCE = new OclMlyLanguage();

    private OclMlyLanguage() {
        super("Mly");
    }
}
