package com.reason.ide.format;

import com.intellij.openapi.application.*;
import com.intellij.openapi.application.ex.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.command.undo.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

public class ReformatOnSave {

  private static final Log LOG = Log.create("format.auto");

  public static void apply(@NotNull Project project, @NotNull Document document) {
    ORSettings settings = ORSettings.getInstance(project);
    if (settings.isFormatOnSaveEnabled()) {
      PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
      if (psiFile != null && psiFile.isWritable()) {
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile != null && virtualFile.exists()) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Before document saving (" + project.getName() + ", autoSave=true): " + psiFile.getVirtualFile());
          }

          String textToReformat = psiFile.getText();
          FormatterProcessor formatterProcessor = ORPostFormatProcessor.getFormatterProcessor(psiFile);
          String newText = formatterProcessor == null ? textToReformat : formatterProcessor.apply(textToReformat);

          if (newText == null || textToReformat.equals(newText)) {
            LOG.debug(" -> Text unchanged, abort format");
          } else {
            ApplicationManagerEx.getApplicationEx()
                .invokeLater(() ->
                                 WriteAction.run(() -> {
                                   CommandProcessor.getInstance()
                                       .executeCommand(project, () -> {
                                             document.setText(newText);
                                             FileDocumentManager.getInstance().saveDocument(document);
                                             PsiFile newFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                                             if (newFile != null) {
                                               newFile.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                                             }
                                           },
                                           "or.reformat",
                                           "CodeFormatGroup",
                                           document);
                                 }));
          }
        }
      }
    }
  }
}
