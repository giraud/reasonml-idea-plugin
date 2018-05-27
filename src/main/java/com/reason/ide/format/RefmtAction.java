package com.reason.ide.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.build.bs.BucklescriptManager;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class RefmtAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();
        if (project != null && (file instanceof OclFile || file instanceof RmlFile)) {
            String format = file instanceof OclFile ? "ml" : "re";
            Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
            if (document != null) {
                BucklescriptManager.getInstance(project).refmt(format, document);
            }
        }
    }
}
