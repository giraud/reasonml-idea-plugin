package com.reason.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import com.reason.ide.files.FileHelper;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class ConvertAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();

        if (project != null && file != null) {
            Bucklescript bucklescript = BucklescriptManager.getInstance(project);
            FileType fileType = file.getFileType();

            if (FileHelper.isReason(fileType)) {
                // convert ReasonML to OCaml
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), "re", "ml", document);
                }
            } else if (FileHelper.isOCaml(fileType)) {
                // convert OCaml to ReasonML
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), "ml", "re", document);
                }
            }
        }
    }
}
