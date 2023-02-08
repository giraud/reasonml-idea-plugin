package com.reason.lang.reason;

import com.intellij.lang.Language;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class RmlLanguage extends Language implements ORLanguageProperties {
    public static final RmlLanguage INSTANCE = new RmlLanguage();

    private RmlLanguage() {
        super("Reason");
    }

    @Override
    public @NotNull String getParameterSeparator() {
        return ", ";
    }

    @Override
    public @NotNull String getFunctionSeparator() {
        return " => ";
    }

    @Override
    public @NotNull String getTemplateStart() {
        return "(";
    }

    @Override
    public @NotNull String getTemplateEnd() {
        return ")";
    }
}
