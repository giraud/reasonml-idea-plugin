package com.reason.ide.format;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoConstants;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.reason.Log;
import com.reason.bs.RefmtProcess;
import com.reason.ide.files.FileHelper;
import com.reason.ide.settings.ORSettings;
import com.reason.ocaml.OCamlFormatProcess;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class ORDocumentFormatter {

  private static final Log LOG = Log.create("document.formatter");

  private final Project m_project;

  private final Set<Pair<Predicate<FileType>, ORFormatCommand>> m_formatCommands;

  public static ORDocumentFormatter getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, ORDocumentFormatter.class);
  }

  public ORDocumentFormatter(Project project) {
    m_project = project;
    ORFormatCommand oCamlFormatCommand = new OCamlFormatCommand(m_project);
    RefmtCommand refmtCommand = new RefmtCommand(m_project);
    m_formatCommands = ImmutableSet.of(
        Pair.create(FileHelper::isOCaml, oCamlFormatCommand),
        Pair.create(FileHelper::isReason, refmtCommand),
        Pair.create(FileHelper::isRescript, refmtCommand)
    );
  }

  public void format(@NotNull PsiFile psiFile) {
    VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile.exists() && psiFile.isWritable()) {
      Optional<ORFormatCommand> command =
          getCommandForFileType(psiFile.getFileType());
      command.ifPresent(formatCommand -> formatWithRetry(psiFile, formatCommand));
    }
  }

  private void formatWithRetry(@NotNull PsiFile psiFile, ORFormatCommand command) {
    formatWithRetry(psiFile, command, 0);
  }

  private void formatWithRetry(@NotNull PsiFile psiFile, ORFormatCommand command, final int retries) {
    Document document = PsiDocumentManager.getInstance(m_project)
        .getDocument(psiFile);
    assert document != null;
    long before = document.getModificationStamp();
    String oldText = document.getText();
    if (!oldText.isEmpty()) {
      String newText = command.execute(psiFile);
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

  private Optional<ORFormatCommand> getCommandForFileType(FileType fileType) {
    return m_formatCommands.stream()
        .filter((pair) -> pair.getFirst().test(fileType))
        .map((pair) -> pair.getSecond())
        .findFirst();
  }

  private interface ORFormatCommand {

    String execute(@NotNull PsiFile psiFile);

  }

  private class RefmtCommand implements ORFormatCommand {

    private final Project m_project;

    public RefmtCommand(Project project) {
      m_project = project;
    }

    @Override
    public String execute(@NotNull PsiFile psiFile) {
      if (ORSettings.getInstance(m_project).isBsEnabled()) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        boolean isInterface = FileHelper.isInterface(psiFile.getFileType());
        String format = isInterface ? "mli" : "ml";
        return refmt.run(psiFile.getVirtualFile(), isInterface, format, psiFile.getText());
      }
      return psiFile.getText();
    }
  }

  private static class OCamlFormatCommand implements ORFormatCommand {

    private final Project m_project;

    public OCamlFormatCommand(Project project) {
      m_project = project;
    }

    @Override
    public String execute(@NotNull PsiFile psiFile) {
      OCamlFormatProcess formatProcess = OCamlFormatProcess.getInstance(m_project);
      return formatProcess.format(psiFile);
    }
  }
}
