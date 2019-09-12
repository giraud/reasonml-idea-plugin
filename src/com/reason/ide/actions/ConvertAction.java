package com.reason.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.Bucklescript;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.reason.ide.files.FileHelper.isInterface;

public class ConvertAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();

        if (project != null && file != null) {
            Bucklescript bucklescript = ServiceManager.getService(project, Bucklescript.class);
            FileType fileType = file.getFileType();

            if (FileHelper.isReason(fileType)) {
                // convert ReasonML to OCaml
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), isInterface(fileType), "re", "ml", document);
                }
            } else if (FileHelper.isOCaml(fileType)) {
                // convert OCaml to ReasonML
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), isInterface(fileType), "ml", "re", document);
                }
            }
        }
    }
}
