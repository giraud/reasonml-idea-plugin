package com.reason.bs;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.reason.ide.LineNumbering;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class BucklescriptErrorAnnotator extends ExternalAnnotator<Collection<BsbError>, Collection<BucklescriptErrorAnnotator.BsbErrorAnnotation>> {

    @Nullable
    @Override
    public Collection<BsbError> collectInformation(@NotNull PsiFile file) {
        Project project = file.getProject();
        String filePath = file.getVirtualFile().getCanonicalPath();
        String projecPath = project.getBaseDir().getCanonicalPath();
        if (filePath == null || projecPath == null) {
            return null;
        }

        String canonicalPath = filePath.substring(projecPath.length() + 1);
        return BucklescriptErrorsManager.getInstance(project).getError(canonicalPath);
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<BsbError> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (BsbError bsbError: collectedInfo) {
            result.add(new BsbErrorAnnotation(bsbError.line - 1, bsbError.colStart - 1, bsbError.colEnd - 1, bsbError.message));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (BsbErrorAnnotation annotation : annotationResult) {
            int startOffset = lineNumbering.positionToOffset(annotation.line, annotation.startOffset);
            int endOffset = lineNumbering.positionToOffset(annotation.line, annotation.endOffset);
            holder.createErrorAnnotation(new TextRangeInterval(startOffset, endOffset), annotation.message);
        }
    }

    static class BsbErrorAnnotation {
        int line;
        final int startOffset;
        final int endOffset;
        String message;

        BsbErrorAnnotation(int line, int startOffset, int endOffset, String message) {
            this.line = line;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.message = message;
        }
    }
}
