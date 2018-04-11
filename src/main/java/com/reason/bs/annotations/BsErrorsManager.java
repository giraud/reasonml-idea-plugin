package com.reason.bs.annotations;

import java.util.Collection;

public abstract class BsErrorsManager {

    abstract public void setError(String file, BsbInfo error);

    abstract public Collection<BsbInfo> getErrors(String filePath);

    abstract public void clearErrors();

    public static class BsbInfo {
        public boolean isError = true;
        public int line;
        public int colStart;
        public int colEnd;
        public String message = "";

        @Override
        public String toString() {
            return "BsbInfo{" +
                    (isError ? "error" : "warning") +
                    ": L" + line + " " + colStart + ":" + colEnd +
                    ", " + message + '}';
        }
    }

}
