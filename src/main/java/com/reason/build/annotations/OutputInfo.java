package com.reason.build.annotations;

public class OutputInfo {
    public boolean isError = true;
    public int lineStart;
    public int colStart;
    public int lineEnd;
    public int colEnd;
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
