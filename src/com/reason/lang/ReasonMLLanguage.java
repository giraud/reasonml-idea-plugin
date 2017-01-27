package com.reason.lang;

import com.intellij.lang.Language;

public class ReasonMLLanguage extends Language {
    public static final ReasonMLLanguage INSTANCE = new ReasonMLLanguage();

    private ReasonMLLanguage() {
        super("ReasonML");
    }
}
