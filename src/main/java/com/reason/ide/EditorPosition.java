package com.reason.ide;

import com.intellij.openapi.editor.*;
import org.jetbrains.annotations.*;

/**
 * Offset <-> Position converter
 */
public class EditorPosition {
    private final int @NotNull [] m_lineLengths;

    /**
     * Extract each line length, needed to compute an offset without a ref to the editor.
     */
    public EditorPosition(String @NotNull [] lines) {
        m_lineLengths = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            m_lineLengths[i] = line.length() + 1 /*line feed*/;
        }
    }

    /**
     * Transform an offset to a logical position (This is an approximation because we don't have
     * access to the editor)
     */
    @Nullable
    public LogicalPosition getPositionFromOffset(int textOffset) {
        int currentLine = 0;
        int totalOffset = 0;
        for (int lineLength : m_lineLengths) {
            if (totalOffset < textOffset && textOffset <= totalOffset + lineLength) {
                break;
            }
            currentLine++;
            totalOffset += lineLength;
        }
        int currentPos = textOffset - totalOffset;

        return currentLine < 0 || currentPos < 0 ? null : new LogicalPosition(currentLine, currentPos);
    }
}
