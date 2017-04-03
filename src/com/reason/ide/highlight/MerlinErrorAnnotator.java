package com.reason.ide.highlight;

import com.google.common.base.Joiner;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.reason.Platform;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinError;
import com.reason.merlin.types.MerlinErrorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerlinErrorAnnotator extends ExternalAnnotator<MerlinInfo, List<MerlinError>> {

    private final Map<MerlinErrorType, HighlightSeverity> severities = new HashMap<>();

    public MerlinErrorAnnotator() {
        severities.put(MerlinErrorType.type, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.parser, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.env, HighlightSeverity.ERROR);
        severities.put(MerlinErrorType.warning, HighlightSeverity.WARNING);
        severities.put(MerlinErrorType.unknown, HighlightSeverity.INFORMATION);
    }

    @Nullable
    @Override
    public MerlinInfo collectInformation(@NotNull PsiFile file) {
        MerlinService merlinService = ProjectManager.getInstance().getOpenProjects()[0/*??*/].getComponent(MerlinService.class);
        return new MerlinInfo(file, file.getText(), merlinService);
    }

    @Nullable
    @Override
    public List<MerlinError> doAnnotate(MerlinInfo collectedInfo) {
        String filename = collectedInfo.getFile().getVirtualFile().getCanonicalPath();
        // BIG HACK
        if (Platform.isWindows()) {
            filename = "file:///mnt/v/sources/reason/ReasonProject/src/" + collectedInfo.getFile().getVirtualFile().getName();
        }

        return collectedInfo.getMerlinService().errors(filename);
    }

    @Override
    public void apply(@NotNull PsiFile file, List<MerlinError> annotationResult, @NotNull AnnotationHolder holder) {
        System.out.println(Joiner.on(", ").join(annotationResult));
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (MerlinError error : annotationResult) {
            int startOffset = lineNumbering.positionToOffset(error.start);
            int endOffset = lineNumbering.positionToOffset(error.end);
            holder.createAnnotation(severities.get(error.type), new TextRangeInterval(startOffset, endOffset), error.message);
        }
    }
}
