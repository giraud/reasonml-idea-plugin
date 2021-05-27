package com.reason;

import org.jetbrains.annotations.*;

import java.util.function.*;

public class Joiner {

    private Joiner() {
    }

    @NotNull
    public static <T> String join(@NotNull String separator, @Nullable Iterable<T> items) {
        return join(separator, items, Object::toString);
    }

    @NotNull
    public static <T> String join(
            @NotNull String separator, @Nullable Iterable<T> items, @NotNull Function<T, String> fn) {
        if (items == null) {
            return "<null>";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(fn.apply(item));
            first = false;
        }
        return sb.toString();
    }

    @NotNull
    public static String join(@NotNull String separator, @Nullable Object[] items) {
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
    public static String joinFrom(@NotNull String separator, @Nullable Object[] items, int from) {
        if (items == null) {
            return "<null>";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = from; i < items.length; i++) {
            if (i != from) {
                sb.append(separator);
            }
            sb.append(items[i]);
        }
        return sb.toString();
    }
}
