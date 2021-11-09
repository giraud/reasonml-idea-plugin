package com.reason.lang;

import com.intellij.lang.*;
import org.jetbrains.annotations.*;

public interface ORLanguageProperties {
    static ORLanguageProperties cast(@Nullable Language language) {
        return (language instanceof ORLanguageProperties) ? (ORLanguageProperties) language : null;
    }

    @NotNull String getParameterSeparator();

    @NotNull String getFunctionSeparator();

    @NotNull String getTemplateStart();

    @NotNull String getTemplateEnd();
}
