package com.reason.build.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.psi.PsiFile;
import com.reason.ide.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ErrorAnnotator extends ExternalAnnotator<Collection<OutputInfo>, Collection<ErrorAnnotator.BsbErrorAnnotation>> {

    private final Debug log = new Debug(Logger.getInstance("ReasonML.build"));

    @Nullable
    @Override
    public Collection<OutputInfo> collectInformation(@NotNull PsiFile file) {
        String filePath = file.getVirtualFile().getCanonicalPath();
        if (filePath != null) {
            return file.getProject().getComponent(ErrorsManager.class).getErrors(filePath);
        }
        return null;
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<OutputInfo> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (OutputInfo info : collectedInfo) {
            result.add(new BsbErrorAnnotation(info.lineStart - 1, info.colStart - 1, info.lineEnd - 1, info.colEnd, info.message, info.isError));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        FileEditorManager fem = FileEditorManager.getInstance(file.getProject());
        TextEditor selectedEditor = (TextEditor) fem.getSelectedEditor(file.getVirtualFile());
        if (selectedEditor != null) {
            Editor editor = selectedEditor.getEditor();
            for (BsbErrorAnnotation annotation : annotationResult) {
                int startOffset = editor.logicalPositionToOffset(annotation.start);
                int endOffset = editor.logicalPositionToOffset(annotation.end);
                if (0 <= startOffset && 0 <= endOffset && startOffset < endOffset) {
                    if (log.isDebugEnabled()) {
                        log.debug("annotate " + startOffset + ":" + endOffset + " '" + annotation.message + "'");
                    }
                    TextRangeInterval range = new TextRangeInterval(startOffset, endOffset);
                    if (annotation.isError) {
                        holder.createErrorAnnotation(range, annotation.message);
                    } else {
                        holder.createWarningAnnotation(range, annotation.message);
                    }
                }
            }
        }
    }

    static class BsbErrorAnnotation {
        LogicalPosition start;
        LogicalPosition end;
        String message;
        boolean isError;

        BsbErrorAnnotation(int lineStart, int startOffset, int lineEnd, int endOffset, String rawMessage, boolean isError) {
            start = new LogicalPosition(lineStart < 0 ? 0 : lineStart, startOffset < 0 ? 0 : startOffset);
            end = new LogicalPosition(lineEnd < 0 ? 0 : lineEnd, endOffset < 0 ? 0 : endOffset);
            message = rawMessage.replace('\n', ' ').replaceAll("\\s+", " ").trim();
            this.isError = isError;
        }
    }
}
