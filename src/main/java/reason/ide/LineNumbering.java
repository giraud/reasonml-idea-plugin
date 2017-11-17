package reason.ide;

import reason.merlin.types.MerlinPosition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Copied from ocaml-ide
public class LineNumbering {

    private List<Integer> lineIndex = new ArrayList<>();

    public LineNumbering(CharSequence buffer) {
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

    public Integer positionToOffset(@NotNull MerlinPosition position) {
        int index = position.line - 1;
        if (index < 0) {
            index = 0;
        }
        int lines = this.lineIndex.size();
        return this.lineIndex.get(index >= lines ? lines : index) + position.col;
    }

    public Integer positionToOffset(int line, int col) {
        return this.lineIndex.get(line) + col;
    }

    public MerlinPosition offsetToPosition(int offset) {
        return this.bisect(offset, 0, this.lineIndex.size() - 1);
    }

    private MerlinPosition bisect(int offset, int start, int end) {
        if (end - start == 1) {
            return new MerlinPosition(start + 1, offset - this.lineIndex.get(start));
        }

        int mid = (start + end) / 2;
        if (between(offset, start, mid)) {
            return bisect(offset, start, mid);
        } else {
            return bisect(offset, mid, end);
        }
    }

    private boolean between(int offset, int start, int end) {
        return this.lineIndex.get(start) <= offset && this.lineIndex.get(end) > offset;
    }
}
