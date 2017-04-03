package com.reason.merlin;

import java.util.List;

public class MerlinError {
    public MerlinPosition start;
    public MerlinPosition end;
    public String type;
    public List sub;
    public boolean valid;
    public String message;

    @Override
    public String toString() {
        return "MerlinError{" +
                "start=" + start.getLine() + ":" + start.getCol() +
                ", end=" + end.getLine() + ":" + end.getCol() +
                ", type='" + type + '\'' +
                ", sub=" + sub +
                ", valid=" + valid +
                ", message='" + message + '\'' +
                '}';
    }
}
