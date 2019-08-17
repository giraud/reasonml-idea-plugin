package com.reason.ide;

import com.intellij.openapi.editor.LogicalPosition;

/**
 * Offset <-> Position converter
 */
public class EditorPosition {
    private int[] m_lineLengths;

    /**
     * Extract each line length, needed to compute an offset without a ref to the editor.
     */
    public EditorPosition(String[] lines) {
        m_lineLengths  = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            m_lineLengths [i] = line.length() + 1/*line feed*/;
        }
    }

    /**
     * Transform an offset to a logical position
     * (This is an approximation because we don't have access to the editor)
     */
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

        return new LogicalPosition(currentLine, currentPos);
    }
}
