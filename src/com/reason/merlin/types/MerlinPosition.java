package com.reason.merlin.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.editor.LogicalPosition;

public class MerlinPosition {
    public int line;
    public int col;

    public MerlinPosition() {
    }

    public MerlinPosition(int line, int col) {
        this.line = line;
        this.col = col;
    }

    public MerlinPosition(LogicalPosition position) {
        this(position.line + 1, position.column);
    }

    public MerlinPosition(JsonNode node) {
        this.line = node.get("line").asInt();
        this.col = node.get("col").asInt();
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public String toShortString() {
        return line + ":" + col;
    }

    @Override
    public String toString() {
        return "{\"line\":" + line + ", \"col\":" + col + "}";
    }
}
