package com.reason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class Joiner {

    @NotNull
    public static String join(@NotNull String separator, @Nullable Iterable<? extends Object> items) {
        if (items == null) {
            return "<null>";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }

    @NotNull
    public static String join(@NotNull String separator, @Nullable String[] items) {
        if (items == null) {
            return "<null>";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }

    public static String join(@NotNull String separator, @Nullable Path[] items) {
        if (items == null) {
            return "<null>";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Path item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }
}
