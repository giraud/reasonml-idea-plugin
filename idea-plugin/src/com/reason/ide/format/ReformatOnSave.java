package com.reason.ide.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.ide.settings.ORSettings;
import org.jetbrains.annotations.NotNull;

public class ReformatOnSave {

  private static final Logger LOG = Logger.getInstance("or.refmt.auto");

  public static void apply(@NotNull Project project, @NotNull Document document) {
    PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);
    ORSettings settings = ORSettings.getInstance(project);
    // Verify this document is part of the project
    if (file != null && settings.isFormatOnSaveEnabled()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Before document saving (" + project.getName() + ", autoSave=" + true + ")");
      }
      ORDocumentFormatter.getInstance(project).format(file);
    }
  }
}
