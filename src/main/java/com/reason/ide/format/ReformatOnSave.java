package com.reason.ide.format;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptManager;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            if (file != null) {
                String format = getFormat(file);
                if (format != null) {
                    m_bs.refmt(format, document);
                }
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
