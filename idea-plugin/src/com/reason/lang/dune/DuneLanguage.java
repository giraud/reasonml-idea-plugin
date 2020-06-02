package com.reason.lang.dune;

import com.intellij.lang.Language;

public class DuneLanguage extends Language {
    public static final DuneLanguage INSTANCE = new DuneLanguage();

    private DuneLanguage() {
        super("Dune");
    }
}

