package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class ResLanguage extends Language implements ORLanguageProperties {
    public static final ResLanguage INSTANCE = new ResLanguage();

    private ResLanguage() {
        super("Rescript");
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
        return "<";
    }

    @Override
    public @NotNull String getTemplateEnd() {
        return ">";
    }
}
