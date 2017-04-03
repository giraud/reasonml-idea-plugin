package com.reason.merlin.types;

import com.reason.merlin.types.MerlinPosition;

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
