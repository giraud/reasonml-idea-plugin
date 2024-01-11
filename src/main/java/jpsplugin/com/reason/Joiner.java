package jpsplugin.com.reason;

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

    public static @NotNull String join(@NotNull String separator, Object @Nullable [] items) {
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
}
