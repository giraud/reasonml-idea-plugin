package com.reason.bs;

class BsbError {
    public String errorType;
    public int line;
    public int colStart;
    public int colEnd;
    public String message;

    @Override
    public String toString() {
        return "BsbError{" +
                ", " + errorType +
                ": L" + line + " " + colStart + ":" + colEnd +
                ", " + message + '}';
    }
}
