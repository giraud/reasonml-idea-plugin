package com.reason.ide.format;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.Bucklescript;
import com.reason.ide.files.FileHelper;
import com.reason.ide.settings.ReasonSettings;

public class ReformatOnSave implements FileDocumentManagerListener {

    private static final Logger LOG = Logger.getInstance("ReasonML.refmt.auto");

    private final PsiDocumentManager m_documentManager;
    private final Project m_project;

    public ReformatOnSave(Project project) {
        m_project = project;
        m_documentManager = PsiDocumentManager.getInstance(m_project);
    }

    /**
     * On save, reformat code using refmt tool.
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        PsiFile file = m_documentManager.getCachedPsiFile(document);
        ReasonSettings reasonSettings = ReasonSettings.getInstance(m_project);

        // Verify this document is part of the project
        if (file != null && reasonSettings.isRefmtOnSaveEnabled()) {
            VirtualFile virtualFile = file.getVirtualFile();
            if (FileHelper.isReason(file.getFileType())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Before document saving (" + m_project.getName() + ", autoSave=" + reasonSettings.isRefmtOnSaveEnabled() + ")");
                }

                String format = ReformatUtil.getFormat(file);
                if (format != null) {
                    ServiceManager.
                            getService(m_project, Bucklescript.class).
                            refmt(virtualFile, FileHelper.isInterface(file.getFileType()), format, document);
                }
            }
        }
    }

    @Override
    public void beforeAllDocumentsSaving() {
    }

    @Override
    public void beforeFileContentReload(@NotNull VirtualFile file, @NotNull Document document) {
    }

    @Override
    public void fileWithNoDocumentChanged(@NotNull VirtualFile file) {
    }

    @Override
    public void fileContentReloaded(@NotNull VirtualFile file, @NotNull Document document) {
    }

    @Override
    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {

    }

    @Override
    public void unsavedDocumentsDropped() {
    }
}
