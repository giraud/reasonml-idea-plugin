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
import com.reason.psi.PsiLet;
import com.reason.psi.PsiValueName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InferredTypes {

    public static void queryForSelectedTextEditor(Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile != null) {
                    Collection<PsiLet> letExpressions = PsiTreeUtil.findChildrenOfType(psiFile, PsiLet.class);

                    boolean useMerlin = true;
                    if (useMerlin) {
                        List<LogicalPosition> positions = letExpressions.stream().
                                map(letExpressionToLogicalPosition(selectedTextEditor)).
                                collect(Collectors.toList());

                        if (!positions.isEmpty()) {
                            MerlinQueryTypesTask inferredTypesTask = new MerlinQueryTypesTask(psiFile, letExpressions, positions);
                            ApplicationManager.getApplication().executeOnPooledThread(inferredTypesTask);
                        }
                    }
                    else {
                        BscInferredTypesTask inferredTypesTask = new BscInferredTypesTask(psiFile, letExpressions);
                        ApplicationManager.getApplication().executeOnPooledThread(inferredTypesTask);
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is disposed but the invokeLater still process that code
            // do nothing
        }
    }

    @NotNull
    private static Function<PsiLet, LogicalPosition> letExpressionToLogicalPosition(Editor selectedTextEditor) {
        return letStatement -> {
            PsiValueName letName = letStatement.getLetName();
            if (letName == null) {
                return null;
            }

            int nameOffset = letName.getTextOffset();
            return selectedTextEditor.offsetToLogicalPosition(nameOffset);
        };
    }

}
