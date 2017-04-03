package com.reason.ide.highlight;

import com.intellij.psi.PsiFile;
import com.reason.merlin.MerlinService;

class MerlinInfo {
    private final PsiFile file;
    private final String buffer;
    private final MerlinService merlinService;

    MerlinInfo(PsiFile file, String buffer, MerlinService merlinService) {
        this.file = file;
        this.buffer = buffer;
        this.merlinService = merlinService;
    }

    PsiFile getFile() {
        return file;
    }

    String getBuffer() {
        return buffer;
    }

    MerlinService getMerlinService() {
        return merlinService;
    }
}
