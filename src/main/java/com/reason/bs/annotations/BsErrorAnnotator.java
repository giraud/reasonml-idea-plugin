package com.reason.bs.annotations;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.impl.TextRangeInterval;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.ide.LineNumbering;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            // Find the PsiElement and attach annotations to it !
            result.add(new BsbErrorAnnotation(bsbError.line - 1, bsbError.colStart - 1, bsbError.colEnd - 1, bsbError.message, bsbError.element));
        }

        return result;
    }


    @Nullable
    public static PsiElement findElementAtOffset(@NotNull PsiFile file, int offset) {
        final List<PsiFile> psiRoots = file.getViewProvider().getAllFiles();
        for (PsiElement root : psiRoots) {
            final PsiElement elementAt = root.findElementAt(offset);
            if (elementAt != null) {
                return elementAt;
            }
        }

        return null;
    }

    @Override
    public void apply(@NotNull PsiFile file, Collection<BsbErrorAnnotation> annotationResult, @NotNull AnnotationHolder holder) {
        LineNumbering lineNumbering = new LineNumbering(file.getText());
        for (BsbErrorAnnotation annotation : annotationResult) {
            PsiElement elementAtOffset = null;
            /*
            if (annotation.m_element != null) {
                elementAtOffset = annotation.m_element;
            } else {
                int startOffset = lineNumbering.positionToOffset(annotation.m_line, annotation.m_startOffset);
                elementAtOffset = findElementAtOffset(file, startOffset);
            }
            */

            if (elementAtOffset != null) {
                holder.createErrorAnnotation(elementAtOffset, annotation.m_message);
                BucklescriptProjectComponent.getInstance(file.getProject()).associatePsiElement(file.getVirtualFile(), elementAtOffset);
            } else {
                int startOffset = lineNumbering.positionToOffset(annotation.m_line, annotation.m_startOffset);
                int endOffset = lineNumbering.positionToOffset(annotation.m_line, annotation.m_endOffset);
                holder.createErrorAnnotation(new TextRangeInterval(startOffset, endOffset), annotation.m_message);
            }
        }
    }

    static class BsbErrorAnnotation {
        int m_line;
        final int m_startOffset;
        final int m_endOffset;
        String m_message;
        PsiElement m_element;

        BsbErrorAnnotation(int line, int startOffset, int endOffset, String message, PsiElement element) {
            m_line = line;
            m_startOffset = startOffset;
            m_endOffset = endOffset;
            m_message = message;
            m_element = element;
        }
    }
}
