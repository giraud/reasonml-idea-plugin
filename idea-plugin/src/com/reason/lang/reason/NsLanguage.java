package com.reason.lang.reason;

import com.intellij.lang.Language;

public class NsLanguage extends Language {
    public static final NsLanguage INSTANCE = new NsLanguage();

    private NsLanguage() {
        super("NapkinScript");
    }
}
