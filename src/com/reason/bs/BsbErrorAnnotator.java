package com.reason.bs;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.psi.PsiFile;
import com.reason.ide.LineNumbering;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class BsbErrorAnnotator extends ExternalAnnotator<Collection<BsbErrorsManager.BsbError>, Collection<BsbErrorAnnotator.BsbErrorAnnotation>> {

    @Nullable
    @Override
    public Collection<BsbErrorsManager.BsbError> collectInformation(@NotNull PsiFile file) {
        String filePath = file.getVirtualFile().getCanonicalPath();
        if (filePath == null) {
            return null;
        }

        return BsbErrorsManager.getInstance(file.getProject()).getErrors(filePath);
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<BsbErrorsManager.BsbError> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (BsbErrorsManager.BsbError bsbError : collectedInfo) {
            // Find the PsiElement and attach annotation to it !
            result.add(new BsbErrorAnnotation(bsbError.line - 1, bsbError.colStart - 1, bsbError.colEnd - 1, bsbError.message));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (BsbErrorAnnotation annotation : annotationResult) {
            int startOffset = lineNumbering.positionToOffset(annotation.m_line, annotation.m_startOffset);
            int endOffset = lineNumbering.positionToOffset(annotation.m_line, annotation.m_endOffset);
            holder.createErrorAnnotation(new TextRangeInterval(startOffset, endOffset), annotation.m_message);
        }
    }

    static class BsbErrorAnnotation {
        int m_line;
        final int m_startOffset;
        final int m_endOffset;
        String m_message;

        BsbErrorAnnotation(int line, int startOffset, int endOffset, String message) {
            m_line = line;
            m_startOffset = startOffset;
            m_endOffset = endOffset;
            m_message = message;
        }
    }
}
