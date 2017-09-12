package com.reason.merlin.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class MerlinError {
    public MerlinPosition start;
    public MerlinPosition end;
    public MerlinErrorType type;
    public String message;

    public MerlinError() {
    }

    public MerlinError(JsonNode node) {
        this.start = new MerlinPosition(node.get("start"));
        this.end = new MerlinPosition(node.get("end"));
        this.type = MerlinErrorType.valueOf(node.get("type").asText());
        this.message = node.get("message").asText();
    }

    @Override
    public String toString() {
        return "MerlinError{" +
                "start=" + start.getLine() + ":" + start.getCol() +
                ", end=" + end.getLine() + ":" + end.getCol() +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
