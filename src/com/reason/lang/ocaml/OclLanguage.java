package com.reason.lang.ocaml;

import com.intellij.lang.Language;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class OclLanguage extends Language implements ORLanguageProperties {
    public static final OclLanguage INSTANCE = new OclLanguage();

    private OclLanguage() {
        super("OCaml");
    }

    @Override
    public @NotNull String getParameterSeparator() {
        return " -> ";
    }

    @Override
    public @NotNull String getFunctionSeparator() {
        return " -> ";
    }

    @Override
    public @NotNull String getTemplateStart() {
        return "";
    }

    @Override
    public @NotNull String getTemplateEnd() {
        return "";
    }
}
