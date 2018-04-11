package com.reason.bs.annotations;

import javax.annotation.Nullable;
import java.util.Collection;

public abstract class BsErrorsManager {

    abstract public void put(@Nullable BsbInfo error);

    abstract public Collection<BsbInfo> getErrors(String filePath);

    abstract public void clearErrors();

    public static class BsbInfo {
        public boolean isError = true;
        public int lineStart;
        public int colStart;
        public int lineEnd;
        public int colEnd;
        public String message = "";
        public String path = "";

        @Override
        public String toString() {
            return "BsbInfo." +
                    (isError ? "ERR" : "WARN") +
                    " -> " + lineStart + ":" + colStart + "-" + lineEnd + ":" + colEnd +
                    (message.isEmpty() ? "" : " / " + message);
        }
    }

}
