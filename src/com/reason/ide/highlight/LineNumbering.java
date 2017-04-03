package com.reason.ide.highlight;

import com.reason.merlin.types.MerlinPosition;

import java.util.ArrayList;
import java.util.List;

// Copied from ocaml-ide
class LineNumbering {

    private List<Integer> lineIndex = new ArrayList<>();

    LineNumbering(CharSequence buffer) {
        this.lineIndex.add(0);
        int i = 0;
        while (i < buffer.length()) {
            if (buffer.charAt(i) == '\n') {
                lineIndex.add(i + 1);
            }
            i++;
        }
        lineIndex.add(Integer.MAX_VALUE);
    }

    Integer positionToOffset(MerlinPosition position) {
        return this.lineIndex.get(position.line - 1) + position.col;
    }
}
