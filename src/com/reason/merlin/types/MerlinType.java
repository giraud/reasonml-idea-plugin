package com.reason.merlin.types;

import java.util.List;

public class MerlinType {
    public MerlinPosition start;
    public MerlinPosition end;
    public String type;
    public String tail; // no | position | call
    public List sub; // not in the doc ?

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
