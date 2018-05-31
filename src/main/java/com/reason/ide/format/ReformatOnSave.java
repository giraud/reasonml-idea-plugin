package com.reason.ide.format;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;

public class ReformatOnSave extends FileDocumentManagerAdapter {

    private final PsiDocumentManager m_documentManager;
    private final Bucklescript m_bs;

    public ReformatOnSave(Project project) {
        m_documentManager = PsiDocumentManager.getInstance(project);
        m_bs = BucklescriptManager.getInstance(project);
    }

    /**
     * On save, reformat code using refmt tool.
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        if (m_bs.isRefmtOnSaveEnabled()) {
            PsiFile file = m_documentManager.getPsiFile(document);
            String format = ReformatUtil.getFormat(file);
            if (format != null) {
                m_bs.refmt(format, document);
            }
        }
    }

}
