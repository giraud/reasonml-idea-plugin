package com.reason.ide.actions;

import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.reason.ide.files.FileHelper.isInterface;

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
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import jpsplugin.com.reason.ORNotification;
import com.reason.comp.bs.BsCompiler;
import com.reason.ide.files.FileHelper;

import java.io.*;

import org.jetbrains.annotations.NotNull;

public class ConvertAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiFile file = e.getData(PSI_FILE);
        Project project = e.getProject();

        if (project != null && file != null) {
            apply(project, file, false);
        }
    }

    protected void apply(@NotNull Project project, @NotNull PsiFile file, boolean isNewFile) {
        BsCompiler bucklescript = ServiceManager.getService(project, BsCompiler.class);
        FileType fileType = file.getFileType();

        final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            final String convertedText;
            final String toFormat;
            VirtualFile sourceFile = file.getVirtualFile();

            boolean isInterface = isInterface(fileType);
            if (FileHelper.isReason(fileType)) {
                // convert ReasonML to OCaml
                toFormat = isInterface ? "mli" : "ml";
                convertedText = bucklescript.convert(sourceFile, isInterface, "re", "ml", document);
            } else if (FileHelper.isOCaml(fileType)) {
                // convert OCaml to ReasonML
                toFormat = isInterface ? "rei" : "re";
                convertedText = bucklescript.convert(sourceFile, isInterface, "ml", "re", document);
            } else {
                toFormat = null;
                convertedText = null;
            }

            if (convertedText != null) {
                CommandProcessor.getInstance()
                        .executeCommand(
                                project,
                                () -> {
                                    WriteAction.run(
                                            () -> {
                                                String newFilename = sourceFile.getNameWithoutExtension() + "." + toFormat;
                                                if (isNewFile) {
                                                    try {
                                                        VirtualFile newSourceFile =
                                                                VfsUtilCore.copyFile(this, sourceFile, sourceFile.getParent(), newFilename);
                                                        PsiFile newPsiFile =
                                                                PsiManager.getInstance(project).findFile(newSourceFile);
                                                        Document newDocument =
                                                                newPsiFile == null
                                                                        ? null
                                                                        : PsiDocumentManager.getInstance(project)
                                                                        .getDocument(newPsiFile);
                                                        if (newDocument != null) {
                                                            newDocument.setText(convertedText);
                                                            FileDocumentManager.getInstance().saveDocument(newDocument);
                                                        }
                                                        VirtualFileManager.getInstance().syncRefresh();
                                                        FileEditorManager.getInstance(project).openFile(newSourceFile, true);
                                                    } catch (IOException ex) {
                                                        Notifications.Bus.notify(
                                                                new ORNotification(
                                                                        "Convert",
                                                                        "File creation failed\n" + ex.getMessage(),
                                                                        NotificationType.ERROR));
                                                    }
                                                } else {
                                                    document.setText(convertedText);
                                                    FileDocumentManager.getInstance().saveDocument(document);
                                                    try {
                                                        sourceFile.rename(this, newFilename);
                                                    } catch (IOException ex) {
                                                        Notifications.Bus.notify(
                                                                new ORNotification(
                                                                        "Convert",
                                                                        "File renaming failed\n" + ex.getMessage(),
                                                                        NotificationType.ERROR));
                                                    }
                                                }
                                            });
                                    file.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                                },
                                "Convert File",
                                "EditMenu",
                                document);
            }
        }
    }
}
