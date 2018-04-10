package com.reason.bs.annotations;

import java.util.Collection;

public abstract class BsErrorsManager {

    abstract public void setError(String file, BsbError error);

    abstract public Collection<BsbError> getErrors(String filePath);

    abstract public void clearErrors();

    public static class BsbError {
        String errorType;
        public int line;
        public int colStart;
        public int colEnd;
        public String message = "";

        @Override
        public String toString() {
            return "BsbError{" +
                    errorType +
                    ": L" + line + " " + colStart + ":" + colEnd +
                    ", " + message + '}';
        }
    }

}
