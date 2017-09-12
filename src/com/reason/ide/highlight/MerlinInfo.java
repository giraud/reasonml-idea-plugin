package com.reason.ide.highlight;

import com.intellij.psi.PsiFile;

class MerlinInfo {
    private final PsiFile m_file;
    private final String m_buffer;

    MerlinInfo(PsiFile file, String buffer) {
        m_file = file;
        m_buffer = buffer;
    }

    PsiFile getFile() {
        return m_file;
    }

    String getBuffer() {
        return m_buffer;
    }
}
