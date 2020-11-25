package com.reason.ide.format;

import com.google.common.collect.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.command.undo.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.bs.*;
import com.reason.dune.*;
import com.reason.ide.files.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class ORDocumentFormatter {

  private static final Set<Pair<Predicate<FileType>, ORFormatCommand>> FORMAT_COMMANDS = ImmutableSet.of(
      Pair.create(FileHelper::isOCaml, new OCamlFormatCommand()),
      Pair.create(FileHelper::isReason, new RefmtCommand()),
      Pair.create(FileHelper::isRescript, new RefmtCommand())
  );

  private final Project m_project;

  public ORDocumentFormatter(@NotNull Project project) {
    m_project = project;
  }

  @NotNull
  public static ORDocumentFormatter getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, ORDocumentFormatter.class);
  }

  public void format(@NotNull PsiFile psiFile) {
    if (psiFile.isWritable()) {
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile != null && virtualFile.exists()) {
        getCommandForFileType(psiFile.getFileType()).ifPresent(formatter -> formatWithRetry(psiFile, formatter, 0));
      }
    }
  }

  private void formatWithRetry(@NotNull PsiFile psiFile, ORFormatCommand command, final int retries) {
    Document document = PsiDocumentManager.getInstance(m_project).getDocument(psiFile);
    if (document != null) {
      long before = document.getModificationStamp();
      String oldText = document.getText();
      if (!oldText.isEmpty()) {
        String newText = command.execute(m_project, psiFile);
        if (newText != null && !newText.isEmpty() && !oldText.equals(newText)) { // additional protection
          ApplicationManager.getApplication()
              .invokeLater(
                  () -> {
                    long after = document.getModificationStamp();
                    if (after > before) {
                      // Document has changed, redo format one time
                      if (retries < 2) {
                        formatWithRetry(psiFile, command, retries + 1);
                      }
                    } else {
                      CommandProcessor.getInstance()
                          .executeCommand(
                              m_project,
                              () -> {
                                WriteAction.run(
                                    () -> {
                                      document.setText(newText);
                                      FileDocumentManager.getInstance().saveDocument(document);
                                    });
                                psiFile.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                              },
                              "or.reformat",
                              "CodeFormatGroup",
                              document);
                    }
                  });
        }
      }
    }
  }

  private @NotNull Optional<ORFormatCommand> getCommandForFileType(@NotNull FileType fileType) {
    return FORMAT_COMMANDS.stream()
               .filter((pair) -> pair.getFirst().test(fileType))
               .map((pair) -> pair.getSecond())
               .findFirst();
  }

  private interface ORFormatCommand {

    @Nullable String execute(@NotNull Project project, @NotNull PsiFile psiFile);

  }

  private static class RefmtCommand implements ORFormatCommand {

    @Override
    public @Nullable String execute(@NotNull Project project, @NotNull PsiFile psiFile) {
      if (ORSettings.getInstance(project).isBsEnabled()) {
        RefmtProcess refmt = RefmtProcess.getInstance(project);
        boolean isInterface = FileHelper.isInterface(psiFile.getFileType());
        String format = isInterface ? "rei" : "re";
        return refmt.run(psiFile.getVirtualFile(), isInterface, format, psiFile.getText());
      }
      return psiFile.getText();
    }
  }

  private static class OCamlFormatCommand implements ORFormatCommand {
    @Override
    public @Nullable String execute(@NotNull Project project, @NotNull PsiFile psiFile) {
      return OcamlFormatProcess.getInstance(project).format(psiFile.getVirtualFile(), psiFile.getText());
    }
  }
}
