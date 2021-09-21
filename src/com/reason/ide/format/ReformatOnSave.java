package com.reason.ide.format;

import com.intellij.openapi.application.*;
import com.intellij.openapi.application.ex.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.command.undo.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

/**
 * IntelliJ doesn't offer an easy way to change document text before it is saved.
 * <p>
 * When we detect that a document is about to be saved, we run an async write command
 * on the main thread that will change the edited document.
 * This write action is run after the initial save (We also use a command to have undo).
 * Then we save the document again, ie we will always generate a minimum of 2 document saves.
 */
public class ReformatOnSave {
    private static final Key<Integer> REFORMAT_COUNT = new Key<>("ReasonML.format.count");
    private static final Log LOG = Log.create("format.auto");

    private ReformatOnSave() {
    }

    public static void apply(@NotNull Project project, @NotNull Document document) {
        ORSettings settings = project.getService(ORSettings.class);
        if (settings.isFormatOnSaveEnabled()) {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null && psiFile.isWritable()) {
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null && virtualFile.exists()) {
                    ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
                    if (!projectFileIndex.isInContent(virtualFile)) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("File " + virtualFile + " not in content root of project " + project + ", skip");
                        }
                        return;
                    }

                    Integer psiCount = psiFile.getUserData(REFORMAT_COUNT);
                    int count = psiCount == null ? 1 : psiCount;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Before document saving (" + project.getName() + ", autoSave=true, count=" + count + "): " + virtualFile);
                    }
                    if (count > 2) {
                        LOG.warn(" -> Too many saves (" + count + "), auto reformat is cancelled");
                        psiFile.putUserData(REFORMAT_COUNT, 1);
                        return;
                    }

                    psiFile.putUserData(REFORMAT_COUNT, count + 1);

                    ApplicationManagerEx.getApplicationEx()
                            .invokeLater(() -> WriteAction.run(() -> {
                                if (!project.isDisposed()) {
                                    PsiFile newFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                                    if (newFile != null) {
                                        String textToReformat = document.getText();
                                        FormatterProcessor formatterProcessor = ORPostFormatProcessor.getFormatterProcessor(newFile);
                                        String newText = formatterProcessor == null ? textToReformat : formatterProcessor.apply(textToReformat);

                                        if (newText == null || textToReformat.equals(newText)) {
                                            LOG.debug(" -> Text null or unchanged, abort format");
                                            newFile.putUserData(REFORMAT_COUNT, 1);
                                        } else {
                                            //noinspection DialogTitleCapitalization
                                            CommandProcessor.getInstance().executeCommand(project, () -> {
                                                        LOG.debug(" -> Applying text formatting");
                                                        UndoUtil.forceUndoIn(virtualFile, () -> document.setText(newText));
                                                    },
                                                    "or.reformat",
                                                    "CodeFormatGroup",
                                                    document);

                                            if (count == 1) {
                                                // Only re-save first time, to avoid infinite loop
                                                FileDocumentManager.getInstance().saveDocument(document);
                                            }
                                        }
                                    }
                                }
                            }));
                }
            }
        }
    }
}
