package com.reason.ide.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class RefmtAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        RefmtManager instance = RefmtManager.getInstance();
        if (instance != null) {
            PsiFile data = e.getData(PSI_FILE);
            Project project = e.getProject();
            if (project != null && data != null) {
                Document document = PsiDocumentManager.getInstance(project).getDocument(data);
                ApplicationManager.getApplication().runWriteAction(() -> instance.refmt(document));
            }
        }
    }
}
