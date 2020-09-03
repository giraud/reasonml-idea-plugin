package com.reason.lang.napkin;

import com.intellij.lang.Language;

public class NsLanguage extends Language {
    public static final NsLanguage INSTANCE = new NsLanguage();

    private NsLanguage() {
        super("NapkinScript");
    }
}
