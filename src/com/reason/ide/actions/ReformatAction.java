package com.reason.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.build.bs.BucklescriptManager;
import com.reason.ide.format.ReformatUtil;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class ReformatAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();
        if (project != null && file != null) {
            String format = ReformatUtil.getFormat(file);
            if (format != null) {
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    BucklescriptManager.getInstance(project).refmt(file.getVirtualFile(), format, document);
                }
            }
        }
    }
}
