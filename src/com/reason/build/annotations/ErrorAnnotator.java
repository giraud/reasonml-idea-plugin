package com.reason.build.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.problems.Problem;
import com.intellij.problems.WolfTheProblemSolver;
import com.intellij.psi.PsiFile;
import com.reason.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ErrorAnnotator extends ExternalAnnotator<Collection<OutputInfo>, Collection<ErrorAnnotator.BsbErrorAnnotation>> {

    private static final Log LOG = Log.create("annotator");

    @Nullable
    @Override
    public Collection<OutputInfo> collectInformation(@NotNull PsiFile file) {
        String filePath = file.getVirtualFile().getCanonicalPath();
        if (filePath != null) {
            return ServiceManager.getService(file.getProject(), ErrorsManager.class).getInfo(filePath);
        }
        return null;
    }

    @Nullable
    @Override
    public Collection<BsbErrorAnnotation> doAnnotate(@NotNull Collection<OutputInfo> collectedInfo) {
        Collection<BsbErrorAnnotation> result = new ArrayList<>();

        for (OutputInfo info : collectedInfo) {
            result.add(new BsbErrorAnnotation(info.lineStart - 1, info.colStart - 1, info.lineEnd - 1, info.colEnd, info.message, info.isError));
        }

        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, @NotNull Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        WolfTheProblemSolver problemSolver = WolfTheProblemSolver.getInstance(file.getProject());
        Collection<Problem> problems = new ArrayList<>();

        FileEditorManager fem = FileEditorManager.getInstance(file.getProject());
        TextEditor selectedEditor = (TextEditor) fem.getSelectedEditor(file.getVirtualFile());
        if (selectedEditor != null) {
            Editor editor = selectedEditor.getEditor();
            for (BsbErrorAnnotation annotation : annotationResult) {
                int startOffset = editor.logicalPositionToOffset(annotation.start);
                int endOffset = editor.logicalPositionToOffset(annotation.end);
                if (0 <= startOffset && 0 <= endOffset && startOffset < endOffset) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("annotate " + startOffset + ":" + endOffset + " '" + annotation.message + "'");
                    }
                    TextRangeInterval range = new TextRangeInterval(startOffset, endOffset);
                    if (annotation.isError) {
                        holder.createErrorAnnotation(range, annotation.message);
                        problems.add(problemSolver.convertToProblem(file.getVirtualFile(), annotation.start.line, annotation.start.column, new String[]{annotation.message}));
                    } else {
                        holder.createWarningAnnotation(range, annotation.message);
                    }
                }
            }
        }

        problemSolver.reportProblems(file.getVirtualFile(), problems);
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
