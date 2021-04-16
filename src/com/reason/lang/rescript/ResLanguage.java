package com.reason.lang.rescript;

import com.intellij.lang.*;

public class ResLanguage extends Language {
    public static final ResLanguage INSTANCE = new ResLanguage();

    private ResLanguage() {
        super("Rescript");
    }
}
