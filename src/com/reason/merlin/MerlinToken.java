package com.reason.merlin;

public class MerlinToken {
    public MerlinPosition start;
    public MerlinPosition end;
    public String token;

    @Override
    public String toString() {
        return "MerlinToken{" +
                "start=" + start +
                ", end=" + end +
                ", token='" + token + '\'' +
                '}';
    }
}
