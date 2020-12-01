package com.reason.ide.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.openapi.util.Pair;
import com.intellij.problems.Problem;
import com.intellij.problems.WolfTheProblemSolver;
import com.intellij.psi.PsiFile;
import com.reason.Log;
import com.reason.Platform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ErrorAnnotator
    extends ExternalAnnotator<
        Collection<ErrorAnnotator.BsbErrorAnnotation>,
        Collection<ErrorAnnotator.BsbErrorAnnotation>> {

  private static final Log LOG = Log.create("annotator");

  @Nullable
  @Override
  public Collection<ErrorAnnotator.BsbErrorAnnotation> collectInformation(
      @NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
    List<BsbErrorAnnotation> result = new ArrayList<>();

    String filename = Platform.getRelativePathToModule(file);

    ErrorsManager errorsManager = ServiceManager.getService(file.getProject(), ErrorsManager.class);
    Collection<OutputInfo> collectedInfo = errorsManager.getInfo(filename);
    if (LOG.isDebugEnabled()) {
      int size = collectedInfo.size();
      LOG.debug("Collected info for file " + filename + ": " + size);
      if (size == 0) {
        Pair<Set<String>, Set<String>> keys = errorsManager.getKeys();
        LOG.debug("  Errors:", keys.first);
        LOG.debug("  Warnings:", keys.second);
      }
    }

    for (OutputInfo info : collectedInfo) {
      LogicalPosition start =
          new LogicalPosition(
              info.lineStart < 1 ? 0 : info.lineStart - 1, info.colStart < 1 ? 0 : info.colStart);
      LogicalPosition end =
          new LogicalPosition(
              info.lineEnd < 1 ? 0 : info.lineEnd - 1, info.colEnd < 1 ? 0 : info.colEnd);
      String message = info.message.replace('\n', ' ').replaceAll("\\s+", " ").trim();

      int startOffset = editor.logicalPositionToOffset(start);
      int endOffset = editor.logicalPositionToOffset(end);
      if (0 < startOffset && 0 < endOffset && startOffset < endOffset) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("annotate " + startOffset + ":" + endOffset + " '" + message + "'");
        }
        TextRangeInterval range = new TextRangeInterval(startOffset - 1, endOffset - 1);
        result.add(new BsbErrorAnnotation(info.isError, message, range, start));
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
              "Failed to locate info: "
                  + start
                  + "->"
                  + end
                  + ", offsets "
                  + startOffset
                  + "->"
                  + endOffset
                  + ", info "
                  + info);
        }
      }
    }

    return result;
  }

  @Nullable
  @Override
  public Collection<BsbErrorAnnotation> doAnnotate(
      @NotNull Collection<BsbErrorAnnotation> collectedInfo) {
    return collectedInfo.isEmpty() ? null : collectedInfo;
  }

  @Override
  public void apply(
      @NotNull PsiFile file,
      @NotNull Collection<BsbErrorAnnotation> annotationResult,
      @NotNull AnnotationHolder holder) {
    WolfTheProblemSolver problemSolver = WolfTheProblemSolver.getInstance(file.getProject());
    Collection<Problem> problems = new ArrayList<>();

    for (BsbErrorAnnotation annotation : annotationResult) {
      if (annotation.isError) {
        holder
            .newAnnotation(HighlightSeverity.ERROR, annotation.message)
            .range(annotation.range)
            .create();
        problems.add(
            problemSolver.convertToProblem(
                file.getVirtualFile(),
                annotation.startPos.line,
                annotation.startPos.column,
                new String[] {annotation.message}));
      } else {
        holder
            .newAnnotation(HighlightSeverity.WARNING, annotation.message)
            .range(annotation.range)
            .create();
      }
    }

    problemSolver.reportProblems(file.getVirtualFile(), problems);
  }

  static class BsbErrorAnnotation {
    final boolean isError;
    final String message;
    final TextRangeInterval range;
    private final LogicalPosition startPos;

    BsbErrorAnnotation(
        boolean isError, String message, TextRangeInterval textRange, LogicalPosition startPos) {
      this.isError = isError;
      this.message = message;
      this.range = textRange;
      this.startPos = startPos;
    }
  }
}