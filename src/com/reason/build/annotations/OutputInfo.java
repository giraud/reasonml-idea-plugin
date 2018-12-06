package com.reason.build.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OutputInfo {
    public boolean isError = true;
    public int lineStart = -1;
    public int colStart = -1;
    public int lineEnd = -1;
    public int colEnd = -1;
    @NotNull
    public String message = "";
    @Nullable
    public String path = "";

    @NotNull
    @Override
    public String toString() {
        return "OutputInfo." +
                (isError ? "ERR" : "WARN") +
                " -> " + lineStart + ":" + colStart + "-" + lineEnd + ":" + colEnd +
                (message.isEmpty() ? "" : " / " + message);
    }
}
