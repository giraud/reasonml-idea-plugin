package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.ReasonMLLet;
import com.reason.psi.ReasonMLValueName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RmlQueryTypes {

    public static void queryForSelectedTextEditor(Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile != null) {
                    Collection<ReasonMLLet> letExpressions = PsiTreeUtil.findChildrenOfType(psiFile, ReasonMLLet.class);
                    List<LogicalPosition> positions = letExpressions.stream().
                            map(letExpressionToLogicalPosition(selectedTextEditor)).
                            collect(Collectors.toList());

                    if (!positions.isEmpty()) {
                        MerlinQueryTypesTask merlinTask = new MerlinQueryTypesTask(psiFile, letExpressions, positions);
                        ApplicationManager.getApplication().executeOnPooledThread(merlinTask); // Let statement has been modified
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is disposed but the invokeLater still process that code
            // do nothing
        }
    }

    @NotNull
    private static Function<ReasonMLLet, LogicalPosition> letExpressionToLogicalPosition(Editor selectedTextEditor) {
        return letStatement -> {
            ReasonMLValueName letName = letStatement.getLetName();
            if (letName == null) {
                return null;
            }

            int nameOffset = letName.getTextOffset();
            return selectedTextEditor.offsetToLogicalPosition(nameOffset);
        };
    }

}
