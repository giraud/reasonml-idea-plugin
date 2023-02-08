package com.reason.lang.doc.reason;

import com.intellij.lang.*;

public class RmlDocLanguage extends Language {
    public static final RmlDocLanguage INSTANCE = new RmlDocLanguage();

    private RmlDocLanguage() {
        super("ReasonDoc");
    }
}
