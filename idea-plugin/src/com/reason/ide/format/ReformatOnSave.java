package com.reason.ide.format;

import com.intellij.openapi.application.*;
import com.intellij.openapi.application.ex.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.command.undo.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

public class ReformatOnSave {
  private static final Key<Integer> REFORMAT_COUNT = new Key<>("reasonml.format.count");
  private static final Log LOG = Log.create("format.auto");

  public static void apply(@NotNull Project project, @NotNull Document document) {
    ORSettings settings = ORSettings.getInstance(project);
    if (settings.isFormatOnSaveEnabled()) {
      PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
      if (psiFile != null && psiFile.isWritable()) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile != null && virtualFile.exists()) {
          Integer count = psiFile.getUserData(REFORMAT_COUNT);
          if (LOG.isDebugEnabled()) {
            LOG.debug("Before document saving (" + project.getName() + ", autoSave=true, count=" + count + "): " + psiFile.getVirtualFile());
          }
          if (count != null && count > 2) {
            LOG.warn("   -> Too many saves (" + count + "), auto reformat is cancelled");
            psiFile.putUserData(REFORMAT_COUNT, 1);
            return;
          }

          String textToReformat = psiFile.getText();
          FormatterProcessor formatterProcessor = ORPostFormatProcessor.getFormatterProcessor(psiFile);
          String newText = formatterProcessor == null ? textToReformat : formatterProcessor.apply(textToReformat);

          if (newText == null || textToReformat.equals(newText)) {
            LOG.debug(" -> Text unchanged, abort format");
            psiFile.putUserData(REFORMAT_COUNT, 1);
          } else {
            ApplicationManagerEx.getApplicationEx()
                .invokeLater(() -> WriteAction.run(() -> {
                  PsiFile newFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                  if (newFile != null) {
                    if (document.getText().equals(newText)) {
                      LOG.debug(" -> Text unchanged, abort format");
                      newFile.putUserData(REFORMAT_COUNT, 1);
                    } else {
                      CommandProcessor.getInstance().executeCommand(project, () -> {
                            document.setText(newText);
                            Integer newCount = newFile.getUserData(REFORMAT_COUNT);
                            newFile.putUserData(REFORMAT_COUNT, newCount == null ? 1 : newCount + 1);
                            newFile.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                            FileDocumentManager.getInstance().saveDocument(document);
                          },
                          "or.reformat",
                          "CodeFormatGroup",
                          document);
                    }
                  }
                }));
          }
        }
      }
    }
  }
}
