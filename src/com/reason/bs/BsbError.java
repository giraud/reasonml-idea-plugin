package com.reason.bs;

import java.util.ArrayList;
import java.util.List;

class BsbError {
    public String file;
    public String errorType;
    public List<String> errors = new ArrayList<>();

    @Override
    public String toString() {
        return "BsbError{" +
                "file='" + file + '\'' +
                ", errorType='" + errorType + '\'' +
                ", errors=" + errors +
                '}';
    }

    public String getCanonicalPath() {
        int pos = file.lastIndexOf('"');
        return file.substring(1, pos);
    }
}
