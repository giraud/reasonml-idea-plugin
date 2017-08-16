package com.reason.merlin.types;

public class MerlinType {
    public MerlinPosition start;
    public MerlinPosition end;
    public String type;
    public String tail; // no | position | call

    @Override
    public String toString() {
        return "MerlinType{" +
                "start=" + start +
                ", end=" + end +
                ", type='" + type + '\'' +
                ", tail='" + tail + '\'' +
                '}';
    }
}
