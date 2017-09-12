package com.reason.ide.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.psi.PsiFile;
import com.reason.ide.LineNumbering;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinError;
import com.reason.merlin.types.MerlinErrorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class MerlinErrorAnnotator extends ExternalAnnotator<MerlinInfo, List<MerlinError>> {

    private final Map<MerlinErrorType, HighlightSeverity> severities = new HashMap<>();

    public MerlinErrorAnnotator() {
        severities.put(MerlinErrorType.error, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.type, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.parser, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.env, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.warning, HighlightSeverity.WARNING);
        severities.put(MerlinErrorType.unknown, HighlightSeverity.INFORMATION);
    }

    @Nullable
    @Override
    public MerlinInfo collectInformation(@NotNull PsiFile file) {
        return new MerlinInfo(file, file.getText());
    }

    @Nullable
    @Override
    public List<MerlinError> doAnnotate(MerlinInfo collectedInfo) {
        MerlinService merlin = ApplicationManager.getApplication().getComponent(MerlinService.class);
        if (merlin != null && merlin.isRunning()) {
            String filename = collectedInfo.getFile().getVirtualFile().getCanonicalPath();
            return merlin.errors(filename, collectedInfo.getBuffer());
        }

        return emptyList();
    }

    @Override
    public void apply(@NotNull PsiFile file, List<MerlinError> annotationResult, @NotNull AnnotationHolder holder) {
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (MerlinError error : annotationResult) {
            if (error.start != null) {
                int startOffset = lineNumbering.positionToOffset(error.start);
                int endOffset = lineNumbering.positionToOffset(error.end);
                if (0 < startOffset && 0 < endOffset && startOffset < endOffset) {
                    holder.createAnnotation(severities.get(error.type), new TextRangeInterval(startOffset, endOffset), "[merlin] " + error.message);
                }
            }
        }
    }
}
