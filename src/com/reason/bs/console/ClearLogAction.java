package com.reason.bs.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;

class ClearLogAction extends DumbAwareAction {
    private ConsoleView m_console;

    ClearLogAction(ConsoleView console) {
        super("Clear All", "Clear the contents of the Event Log", AllIcons.Actions.GC);
        m_console = console;
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        m_console.clear();
    }
}
