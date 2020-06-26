package com.reason.ide.format;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.BsCompiler;
import com.reason.ide.files.FileHelper;
import com.reason.ide.settings.ORSettings;

public class ReformatOnSave {
    private static final Logger LOG = Logger.getInstance("ReasonML.refmt.auto");

    public static void apply(@NotNull Project project, @NotNull Document document) {
        PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);
        ORSettings ORSettings = com.reason.ide.settings.ORSettings.getInstance(project);

        // Verify this document is part of the project
        if (file != null && ORSettings.isFormatOnSaveEnabled()) {
            VirtualFile virtualFile = file.getVirtualFile();
            if (FileHelper.isReason(file.getFileType())) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Before document saving (" + project.getName() + ", autoSave=" + ORSettings.isFormatOnSaveEnabled() + ")");
                }

                String format = ReformatUtil.getFormat(file);
                if (format != null) {
                    ServiceManager.
                            getService(project, BsCompiler.class).
                            refmt(virtualFile, FileHelper.isInterface(file.getFileType()), format, document);
                }
            }
        }
    }
}
