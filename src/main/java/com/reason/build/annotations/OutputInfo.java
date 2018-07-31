package com.reason.build.annotations;

public class OutputInfo {
    public boolean isError = true;
    public int lineStart = -1;
    public int colStart = -1;
    public int lineEnd = -1;
    public int colEnd = -1;
    public String message = "";
    public String path = "";

    @Override
    public String toString() {
        return "OutputInfo." +
                (isError ? "ERR" : "WARN") +
                " -> " + lineStart + ":" + colStart + "-" + lineEnd + ":" + colEnd +
                (message.isEmpty() ? "" : " / " + message);
    }
}
