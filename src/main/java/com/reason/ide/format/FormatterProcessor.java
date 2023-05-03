package com.reason.ide.format;

import org.jetbrains.annotations.*;

public interface FormatterProcessor {
    @Nullable String apply(@NotNull String textToFormat);
}
