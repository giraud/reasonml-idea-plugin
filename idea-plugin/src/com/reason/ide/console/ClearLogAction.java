package com.reason.ide.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

class ClearLogAction extends DumbAwareAction {
  private final ConsoleView m_console;

  ClearLogAction(ConsoleView console) {
    super("Clear All", "Clear the contents of the logs", AllIcons.Actions.GC);
    m_console = console;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    Editor editor = e.getData(CommonDataKeys.EDITOR);
    e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    m_console.clear();
  }
}
