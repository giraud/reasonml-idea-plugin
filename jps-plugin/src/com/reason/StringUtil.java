package com.reason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class StringUtil {
    private StringUtil() {
    }

    @NotNull
    public static String toFirstUpper(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return value.substring(0, 1).toUpperCase(Locale.getDefault()) + value.substring(1);
    }
}
