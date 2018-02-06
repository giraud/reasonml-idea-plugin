package com.reason.bs.annotations;

import java.util.ArrayList;
import java.util.List;

// TODO: delete that class
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

    Integer positionToOffset(int line, int col) {
        return this.lineIndex.get(line) + col;
    }

}
