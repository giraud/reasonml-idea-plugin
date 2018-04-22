package com.reason.lang.reason;

import com.intellij.lang.Language;

public class RmlLanguage extends Language {
    public static final RmlLanguage INSTANCE = new RmlLanguage();

    private RmlLanguage() {
        super("ReasonML");
    }
}
