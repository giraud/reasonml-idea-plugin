package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.highlight.MlSyntaxHighlighter;
import com.reason.lang.core.psi.PsiLet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class RmlEditorLinePainter extends EditorLinePainter {

    @Override
    public Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) {
            return null;
        }

        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor == null) {
            return null;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);

        //int lineStartOffset = document.getLineStartOffset(lineNumber - 1);
        //PsiLet elementOfClassAtOffset = PsiTreeUtil.findElementOfClassAtOffset(psiFile, lineStartOffset, PsiLet.class, false);
        //System.out.println("line: " + lineNumber + " (" + lineStartOffset + ") " + elementOfClassAtOffset);

        Collection<PsiLet> letStatements = PsiTreeUtil.findChildrenOfType(psiFile, PsiLet.class);

        final String[] inferredType = {null};
        for (PsiLet letStatement : letStatements) {
            int letOffset = letStatement.getTextOffset();
            ApplicationManager.getApplication().runReadAction(() -> {
                LogicalPosition letLogicalPosition = selectedTextEditor.offsetToLogicalPosition(letOffset);
                if (letLogicalPosition.line == lineNumber) {
                    inferredType[0] = letStatement.getInferredType();
                }
            });
        }

        if (inferredType[0] == null) {
            return null;
        }

        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        //TextAttributes attributes = getNormalAttributes();
        TextAttributes codeLens = globalScheme.getAttributes(MlSyntaxHighlighter.CODE_LENS_);
        LineExtensionInfo info = new LineExtensionInfo("  " + inferredType[0], codeLens);
        return Collections.singletonList(info);
    }

    //private static TextAttributes getNormalAttributes() {
    //    EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
    //    TextAttributes attributes = globalScheme.getAttributes(DebuggerColors.INLINED_VALUES);
    //    TextAttributes codeLens = globalScheme.getAttributes(MlSyntaxHighlighter.CODE_LENS_);
    //
    //    if (attributes == null || attributes.getForegroundColor() == null) {
    //        return new TextAttributes(codeLens.getForegroundColor(), codeLens, null, null, Font.ITALIC);
    //    }
    //    return attributes;
    //}

}
