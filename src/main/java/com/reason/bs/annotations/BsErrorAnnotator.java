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

public class BsErrorAnnotator extends ExternalAnnotator<Collection<BsErrorsManager.BsbInfo>, Collection<BsErrorAnnotator.BsbErrorAnnotation>> {

    @Nullable
    @Override
    public Collection<BsErrorsManager.BsbInfo> collectInformation(@NotNull PsiFile file) {
        String filePath = file.getVirtualFile().getCanonicalPath();
        if (filePath != null) {
            return BucklescriptProjectComponent.getInstance(file.getProject()).getErrors(filePath);
        }
        return null;
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(Collection<BsErrorsManager.BsbInfo> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (BsErrorsManager.BsbInfo info : collectedInfo) {
            result.add(new BsbErrorAnnotation(info.line - 1, info.colStart - 1, info.colEnd, info.message, info.isError));
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

        BsbErrorAnnotation(int line, int startOffset, int endOffset, String rawMessage, boolean isError) {
            start = new LogicalPosition(line, startOffset);
            end = new LogicalPosition(line, endOffset);
            message = rawMessage.replace('\n', ' ').replaceAll("\\s+", " ").trim();
            this.isError = isError;
        }
    }
}
