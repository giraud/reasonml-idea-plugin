package com.reason.merlin.types;

public class MerlinVersion {
    public int selected;
    public int latest;
    public String merlin;

    @Override
    public String toString() {
        return merlin + " (protocol: " + selected + ")";
    }
}
