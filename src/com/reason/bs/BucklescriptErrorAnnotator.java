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
import java.util.stream.Collectors;

public class BucklescriptErrorAnnotator extends ExternalAnnotator<Collection<BsbError>, Collection<BucklescriptErrorAnnotator.BsbErrorAnnotation>> {

    @Nullable
    @Override
    public Collection<BsbError> collectInformation(@NotNull PsiFile file) {
        String canonicalPath = file.getVirtualFile().getCanonicalPath();
        return BucklescriptErrorsManager.getInstance(file.getProject()).getError(canonicalPath);
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<BsbError> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (BsbError infoEntry : collectedInfo) {
            String file = infoEntry.file;
            String[] tokens = file.split(", ");
            int line = Integer.parseInt(tokens[1].substring(5));
            String[] cols = tokens[2].substring(11).split("-");
            int startOffset = Integer.parseInt(cols[0]);
            int endOffset = Integer.parseInt(cols[1].substring(0, cols[1].length() - 1));

            result.addAll(infoEntry.errors.stream().map(s -> new BsbErrorAnnotation(line, startOffset, endOffset, s)).collect(Collectors.toList()));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (BsbErrorAnnotation annotation : annotationResult) {
            int startOffset = lineNumbering.positionToOffset(annotation.line - 1, annotation.startOffset);
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
