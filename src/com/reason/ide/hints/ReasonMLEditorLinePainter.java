package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.xdebugger.ui.DebuggerColors;
import com.reason.ide.LineNumbering;
import com.reason.merlin.types.MerlinPosition;
import com.reason.psi.ReasonMLLetStatement;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

public class ReasonMLEditorLinePainter extends EditorLinePainter {

    @Override
    public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) {
            return null;
        }

        // Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        // if (selectedTextEditor == null) {
        //     return null;
        // }
        // Application application = ApplicationManager.getApplication();

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        LineNumbering lineNumbering = new LineNumbering(document.getCharsSequence());

        Collection<ReasonMLLetStatement> letStatements = PsiTreeUtil.findChildrenOfType(psiFile, ReasonMLLetStatement.class);

        Function<ReasonMLLetStatement, String> findInferredType = letStatement -> {
            // Found a let statement, try to get its type if in correct line number
            final int[] letOffset = new int[] {-1};
            // ApplicationManager.getApplication().runReadAction(() -> { // Freezing pb
                letOffset[0] = letStatement.getTextOffset();
            // });
            // TODO: I'm using the LineNumbering class to avoid frequent exceptions about read access,
            // but I would prefer to use runReadAction method.
            MerlinPosition letPosition = lineNumbering.offsetToPosition(letOffset[0]);
//            LogicalPosition letPosition = new LogicalPosition;
//            ApplicationManager.getApplication().runReadAction(() -> {
//                letPosition[0] = selectedTextEditor.offsetToLogicalPosition(letOffset);
//            });
            return letPosition.line - 1 == lineNumber ? letStatement.getInferredType() : null;
        };


        String inferredType;
        inferredType = letStatements.parallelStream().map(findInferredType).filter(Objects::nonNull).findFirst().orElse(null);
        if (inferredType == null) {
            return null;
        }

        final TextAttributes attributes = getNormalAttributes();
        LineExtensionInfo info = new LineExtensionInfo("  " + inferredType, attributes);
        return Collections.singletonList(info);
    }

    private static TextAttributes getNormalAttributes() {
        TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(DebuggerColors.INLINED_VALUES);
        if (attributes == null || attributes.getForegroundColor() == null) {
            return new TextAttributes(new JBColor(Gray._135, new Color(0x3d8065)), null, null, null, Font.ITALIC);
        }
        return attributes;
    }

}
