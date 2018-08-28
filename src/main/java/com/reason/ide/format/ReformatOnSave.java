package com.reason.ide.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;

public class ReformatOnSave extends FileDocumentManagerAdapter {

    private static final Logger LOG = Logger.getInstance("ReasonML.refmt.auto");

    private final PsiDocumentManager m_documentManager;
    private final Bucklescript m_bs;
    private final Project m_project;

    public ReformatOnSave(Project project) {
        m_project = project;
        m_documentManager = PsiDocumentManager.getInstance(m_project);
        m_bs = BucklescriptManager.getInstance(m_project);
    }

    /**
     * On save, reformat code using refmt tool.
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Before document saving (" + m_project.getName() + ", autoSave=" + m_bs.isRefmtOnSaveEnabled() + ")");
        }

        // verify this document is part of the project
        PsiFile file = m_documentManager.getCachedPsiFile(document);
        if (file != null && m_bs.isRefmtOnSaveEnabled()) {
            VirtualFile virtualFile = file.getVirtualFile();
            String format = ReformatUtil.getFormat(file);
            if (format != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reformating " + file.getVirtualFile().getPath() + " (" + format + ")");
                }
                m_bs.refmt(virtualFile, format, document);
            }
        }
    }

}
