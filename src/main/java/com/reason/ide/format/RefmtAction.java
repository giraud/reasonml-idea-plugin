package com.reason.ide.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.OclFile;
import com.reason.RmlFile;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class RefmtAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        RefmtManager refmt = RefmtManager.getInstance();
        if (refmt != null) {
            PsiFile file = e.getData(PSI_FILE);
            Project project = e.getProject();
            if (project != null && file != null && (file instanceof OclFile || file instanceof RmlFile)) {
                String format = file instanceof OclFile ? "ml" : "re";
                Document document = PsiDocumentManager.getInstance(project).getDocument(file);
                if (document != null) {
                    WriteCommandAction.writeCommandAction(project).run(() -> {
                        refmt.refmt(project, format, document);
                    });
                }
            }
        }
    }
}
