package com.reason.ide.format;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReformatOnSave extends FileDocumentManagerAdapter {

    private final Project m_project;

    public ReformatOnSave(Project project) {
        m_project = project;
    }

    /**
     * On save, reformat code using refmt tool.
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        PsiFile file = PsiDocumentManager.getInstance(m_project).getPsiFile(document);
        if (file != null) {
            String format = getFormat(file);
            if (format != null) {
                RefmtManager.getInstance().refmt(m_project, format, document);
            }
        }
    }

    @Nullable
    private String getFormat(PsiFile file) {
        String format = null;
        if (file instanceof OclFile) {
            format = "ml";
        } else if (file instanceof RmlFile) {
            format = "re";
        }
        return format;
    }
}
