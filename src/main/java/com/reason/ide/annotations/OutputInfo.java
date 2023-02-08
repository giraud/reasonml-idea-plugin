package com.reason.ide.annotations;

import org.jetbrains.annotations.*;

public class OutputInfo {
    public boolean isError = false;
    public int lineStart = -1;
    public int colStart = -1;
    public int lineEnd = -1;
    public int colEnd = -1;
    @NotNull public String message = "";
    @Nullable public String path = "";


    @Override
    public @NotNull String toString() {
        return "OutputInfo."
                + (isError ? "ERR" : "WARN") + " -> "
                + lineStart + ":" + colStart + "-"
                + lineEnd + ":" + colEnd
                + (message.isEmpty() ? " «empty message»" : " / " + message);
    }
}
