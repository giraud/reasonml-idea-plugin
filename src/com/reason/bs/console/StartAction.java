package com.reason.bs.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.reason.bs.BsbCompiler;

public class StartAction extends DumbAwareAction {
    private final BsbCompiler m_bsc;
    private final ConsoleView m_console;
    private boolean m_enable = false;

    StartAction(ConsoleView console, BsbCompiler bsc) {
        super("Start", "Start bucklescript process", AllIcons.Actions.Execute);
        m_bsc = bsc;
        m_console = console;
    }

    void setEnable(boolean value) {
        m_enable = value;
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(m_enable);
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        m_enable = false;
        m_console.attachToProcess(m_bsc.recreate());
        m_bsc.startNotify();
    }
}
