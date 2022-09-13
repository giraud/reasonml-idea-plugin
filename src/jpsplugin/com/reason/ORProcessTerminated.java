package jpsplugin.com.reason;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface ORProcessTerminated<T> {
    void run(@Nullable T data);
}
