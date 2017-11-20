package com.reason.merlin.types;

import com.fasterxml.jackson.databind.JsonNode;

public class MerlinType {
    public MerlinPosition start;
    public MerlinPosition end;
    public String type;
    public String tail; // no | position | call

    public MerlinType(JsonNode node) {
        this.start = new MerlinPosition(node.get("start"));
        this.end = new MerlinPosition(node.get("end"));
        this.type = node.get("type").textValue();
    }

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
