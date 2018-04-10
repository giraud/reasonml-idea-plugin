package com.reason.bs.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.psi.PsiFile;
import com.reason.bs.BucklescriptProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class BsErrorAnnotator extends ExternalAnnotator<Collection<BsErrorsManager.BsbError>, Collection<BsErrorAnnotator.BsbErrorAnnotation>> {

    @Nullable
    @Override
    public Collection<BsErrorsManager.BsbError> collectInformation(@NotNull PsiFile file) {
        String filePath = file.getVirtualFile().getCanonicalPath();
        if (filePath == null) {
            return null;
        }

        return BucklescriptProjectComponent.getInstance(file.getProject()).getErrors(filePath);
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<BsErrorsManager.BsbError> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (BsErrorsManager.BsbError bsbError : collectedInfo) {
            result.add(new BsbErrorAnnotation(bsbError.line - 1, bsbError.colStart - 1, bsbError.colEnd, bsbError.message));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        FileEditorManager fem = FileEditorManager.getInstance(file.getProject());
        TextEditor selectedEditor = (TextEditor) fem.getSelectedEditor(file.getVirtualFile());
        if (selectedEditor != null) {
            Logger log = Logger.getInstance("ReasonML.build");
            Editor editor = selectedEditor.getEditor();
            for (BsbErrorAnnotation annotation : annotationResult) {
                int startOffset = editor.logicalPositionToOffset(annotation.start);
                int endOffset = editor.logicalPositionToOffset(annotation.end);
                if (0 <= startOffset && 0 <= endOffset && startOffset < endOffset) {
                    log.info("annotate " + startOffset + ":" + endOffset + " '" + annotation.message + "'");
                    holder.createErrorAnnotation(new TextRangeInterval(startOffset, endOffset), annotation.message);
                }
            }
        }
    }

    static class BsbErrorAnnotation {
        LogicalPosition start;
        LogicalPosition end;
        String message;

        BsbErrorAnnotation(int line, int startOffset, int endOffset, String rawMessage) {
            start = new LogicalPosition(line, startOffset);
            end = new LogicalPosition(line, endOffset);
            message = rawMessage.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        }
    }
}
