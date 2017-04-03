package com.reason.ide.highlight;

import com.intellij.psi.PsiFile;

class MerlinInfo {
    private final PsiFile file;
    private final String buffer;

    MerlinInfo(PsiFile file, String buffer) {
        this.file = file;
        this.buffer = buffer;
    }

    PsiFile getFile() {
        return file;
    }

    String getBuffer() {
        return buffer;
    }
}
