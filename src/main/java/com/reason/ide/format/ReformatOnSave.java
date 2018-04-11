package com.reason.ide.format;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.OclFile;
import org.jetbrains.annotations.NotNull;

public class ReformatOnSave extends FileDocumentManagerAdapter {

    private final Project m_project;

    public ReformatOnSave(Project project) {
        m_project = project;
    }

    /**
     * On save, reformat code using refmt tool.
     *
     * @param document Document that is being saved
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        PsiFile file = PsiDocumentManager.getInstance(m_project).getPsiFile(document);
        String format = file instanceof OclFile ? "ml" : "re";
        WriteCommandAction.writeCommandAction(m_project).run(() -> {
            RefmtManager.getInstance().refmt(m_project, format, document);
        });
    }
}
