package com.reason.ide;

import static com.intellij.AppTopics.FILE_DOCUMENT_SYNC;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.format.ReformatOnSave;
import org.jetbrains.annotations.NotNull;

public class ORFileDocumentListener {

  public static void ensureSubscribed(@NotNull Project project) {
    ServiceManager.getService(project, ORFileDocumentListener.class);
  }

  private ORFileDocumentListener(@NotNull Project project) {
    project
        .getMessageBus()
        .connect(project)
        .subscribe(
            FILE_DOCUMENT_SYNC,
            new FileDocumentManagerListener() {
              @Override
              public void beforeAllDocumentsSaving() {}

              @Override
              public void beforeDocumentSaving(@NotNull Document document) {
                // On save, reformat code using refmt tool.
                ReformatOnSave.apply(project, document);
              }

              @Override
              public void beforeFileContentReload(
                  @NotNull VirtualFile file, @NotNull Document document) {}

              @Override
              public void fileWithNoDocumentChanged(@NotNull VirtualFile file) {}

              @Override
              public void fileContentReloaded(
                  @NotNull VirtualFile file, @NotNull Document document) {}

              @Override
              public void fileContentLoaded(
                  @NotNull VirtualFile file, @NotNull Document document) {}

              @Override
              public void unsavedDocumentsDropped() {}
            });
  }
}
