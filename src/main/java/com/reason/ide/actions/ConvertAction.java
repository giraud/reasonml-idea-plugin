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
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.OclInterfaceFileType;
import com.reason.ide.files.RmlFileType;
import com.reason.ide.files.RmlInterfaceFileType;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

public class ConvertAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();

        if (project != null && file != null) {
            Bucklescript bucklescript = BucklescriptManager.getInstance(project);
            FileType fileType = file.getFileType();

            if (fileType instanceof RmlFileType || fileType instanceof RmlInterfaceFileType) {
                // convert ReasonML to OCaml
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), "re", "ml", document);
                }
            } else if (fileType instanceof OclFileType || fileType instanceof OclInterfaceFileType) {
                // convert OCaml to ReasonML
                Document document = PsiDocumentManager.getInstance(project).getCachedDocument(file);
                if (document != null) {
                    bucklescript.convert(file.getVirtualFile(), "ml", "re", document);
                }
            }
        }
    }
}
