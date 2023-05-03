package com.reason.ide.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static com.intellij.openapi.actionSystem.CommonDataKeys.*;
import static com.reason.ide.files.FileHelper.*;

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
        BsCompiler bucklescript = project.getService(BsCompiler.class);
        FileType fileType = file.getFileType();

        final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        if (document != null) {
            String convertedText;
            String toFormat;
            VirtualFile sourceFile = ORFileUtils.getVirtualFile(file);

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

            if (sourceFile != null && convertedText != null) {
                CommandProcessor.getInstance()
                        .runUndoTransparentAction(
                                () -> WriteAction.run(
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
                                        }));
            }
        }
    }
}
