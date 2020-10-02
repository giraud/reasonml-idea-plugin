package com.reason.ide.repl;

import static com.intellij.execution.ui.ConsoleViewContentType.USER_INPUT;
import static com.intellij.openapi.editor.EditorKind.CONSOLE;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.SideBorder;
import com.reason.lang.ocaml.OclLanguage;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

final class PromptConsole implements Disposable {
  private static final String PROMPT_INACTIVE = "  ";

  private final JPanel m_mainPanel = new JPanel(new BorderLayout());
  private final ConsoleViewImpl m_consoleView;
  @NotNull private final EditorImpl m_outputEditor;
  @NotNull private final EditorImpl m_promptEditor;
  private final PromptHistory m_history = new PromptHistory(100);
  private boolean m_promptEnabled = true;

  PromptConsole(@NotNull Project project, ConsoleViewImpl consoleView) {
    m_consoleView = consoleView;

    EditorFactory editorFactory = EditorFactory.getInstance();
    PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

    Document outputDocument = ((EditorFactoryImpl) editorFactory).createDocument(true);
    UndoUtil.disableUndoFor(outputDocument);
    m_outputEditor = (EditorImpl) editorFactory.createViewer(outputDocument, project, CONSOLE);

    PsiFile file =
        fileFactory.createFileFromText("PromptConsoleDocument.ml", OclLanguage.INSTANCE, "");
    Document promptDocument = file.getViewProvider().getDocument();
    m_promptEditor = (EditorImpl) editorFactory.createEditor(promptDocument, project, CONSOLE);

    setupOutputEditor();
    setupPromptEditor();

    m_consoleView.print(
        "(* ctrl+enter to send a command, ctrl+up/down to cycle through history *)\r\n",
        USER_INPUT);

    m_mainPanel.add(m_outputEditor.getComponent(), BorderLayout.CENTER);
    m_mainPanel.add(m_promptEditor.getComponent(), BorderLayout.SOUTH);
  }

  @Override
  public void dispose() {
    EditorFactory editorFactory = EditorFactory.getInstance();
    editorFactory.releaseEditor(m_outputEditor);
    editorFactory.releaseEditor(m_promptEditor);
  }

  @NotNull
  JComponent getCenterComponent() {
    return m_mainPanel;
  }

  @NotNull
  EditorImpl getOutputEditor() {
    return m_outputEditor;
  }

  @NotNull
  JComponent getInputComponent() {
    return m_promptEditor.getContentComponent();
  }

  void disablePrompt() {
    m_promptEnabled = false;
    m_promptEditor.setPrefixTextAndAttributes(PROMPT_INACTIVE, USER_INPUT.getAttributes());
    m_promptEditor.getDocument().setText("");
    m_promptEditor.setRendererMode(true);
  }

  private void setupOutputEditor() {
    ((EditorEx) m_outputEditor).getContentComponent().setFocusCycleRoot(false);
    ((EditorEx) m_outputEditor).setHorizontalScrollbarVisible(true);
    ((EditorEx) m_outputEditor).setVerticalScrollbarVisible(true);
    ((EditorEx) m_outputEditor).getScrollPane().setBorder(null);

    EditorSettings editorSettings = ((EditorEx) m_outputEditor).getSettings();
    editorSettings.setLineNumbersShown(false);
    editorSettings.setIndentGuidesShown(false);
    editorSettings.setLineMarkerAreaShown(false);
    editorSettings.setFoldingOutlineShown(true);
    editorSettings.setRightMarginShown(false);
    editorSettings.setVirtualSpace(false);
    editorSettings.setAdditionalPageAtBottom(false);
    editorSettings.setAdditionalLinesCount(0);
    editorSettings.setAdditionalColumnsCount(0);
    editorSettings.setLineCursorWidth(1);
    editorSettings.setCaretRowShown(false);

    // output only editor
    m_outputEditor.setRendererMode(true);

    // tiny separation between output and prompt
    m_outputEditor.getComponent().setBorder(new SideBorder(JBColor.LIGHT_GRAY, SideBorder.BOTTOM));
  }

  private void setupPromptEditor() {
    EditorSettings editorSettings = ((EditorEx) m_promptEditor).getSettings();
    editorSettings.setAdditionalLinesCount(0);

    m_promptEditor.getComponent().setPreferredSize(new Dimension(0, 100));

    // add copy/paste actions
    m_promptEditor.addEditorMouseListener(
        EditorActionUtil.createEditorPopupHandler(IdeActions.GROUP_CUT_COPY_PASTE));

    // hook some key event on prompt editor
    m_promptEditor
        .getContentComponent()
        .addKeyListener(
            new KeyAdapter() {
              @Override
              public void keyReleased(@NotNull KeyEvent e) {
                if (m_promptEnabled && e.isControlDown()) {
                  int keyCode = e.getKeyCode();
                  if (keyCode == KeyEvent.VK_ENTER) {
                    String command = normalizeCommand(m_promptEditor.getDocument().getText());
                    m_history.addInHistory(command);
                    m_consoleView.print(command + "\r\n", USER_INPUT);
                    m_consoleView.scrollToEnd();
                    ApplicationManager.getApplication().runWriteAction(() -> setPromptCommand(""));
                  } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
                    String history = m_history.getFromHistory(keyCode == KeyEvent.VK_DOWN);
                    if (history != null) {
                      ApplicationManager.getApplication()
                          .runWriteAction(() -> setPromptCommand(history));
                    }
                  }
                }
              }
            });
  }

  @NotNull
  private String normalizeCommand(@NotNull String command) {
    String sanitizedCommand = command.trim();
    if (!sanitizedCommand.endsWith(";;")) {
      if (!sanitizedCommand.endsWith(";")) {
        sanitizedCommand += ";;";
      } else {
        sanitizedCommand += ";";
      }
    }

    return sanitizedCommand;
  }

  private void setPromptCommand(@NotNull String text) {
    m_promptEditor.getDocument().setText(text);
    m_promptEditor.getScrollingModel().scrollHorizontally(0);
    m_promptEditor.getCaretModel().moveToOffset(text.length());
  }
}
