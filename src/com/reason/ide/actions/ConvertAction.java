package com.reason.ide.actions;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoConstants;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.bs.Bucklescript;
import com.reason.ide.ORNotification;
import com.reason.ide.files.FileHelper;

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

            final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
            if (document != null) {
                final String convertedText;
                final String toFormat;
                VirtualFile virtualFile = file.getVirtualFile();

                boolean isInterface = isInterface(fileType);
                if (FileHelper.isReason(fileType)) {
                    // convert ReasonML to OCaml
                    toFormat = isInterface ? "mli" : "ml";
                    convertedText = bucklescript.convert(virtualFile, isInterface, "re", "ml", document);
                } else if (FileHelper.isOCaml(fileType)) {
                    // convert OCaml to ReasonML
                    toFormat = isInterface ? "rei" : "re";
                    convertedText = bucklescript.convert(virtualFile, isInterface, "ml", "re", document);
                } else {
                    convertedText = null;
                    toFormat = null;
                }

                if (convertedText != null) {
                    CommandProcessor.getInstance().executeCommand(project, () -> {
                        WriteAction.run(() -> {
                            document.setText(convertedText);
                            FileDocumentManager.getInstance().saveDocument(document);
                            try {
                                virtualFile.rename(this, virtualFile.getNameWithoutExtension() + "." + toFormat);
                            } catch (IOException ex) {
                                Notifications.Bus.notify(new ORNotification("Bsb", "Can't convert file\n" + ex.getMessage(), NotificationType.ERROR));
                            }
                        });
                        file.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                    }, "reason.convert", "CodeFormatGroup", document);
                }
            }
        }
    }
}
