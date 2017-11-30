package com.reason.bs.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.reason.bs.BsCompiler;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;

public class StartAction extends DumbAwareAction {

    private final ConsoleView m_console;
    private final Project m_project;
    private boolean m_enable = false;

    StartAction(ConsoleView console, Project project) {
        super("Start", "Start bucklescript process", AllIcons.Actions.Execute);
        m_project = project;
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
        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(m_project);
        BsCompiler bsc = bucklescript.getCompiler();
        m_console.attachToProcess(bsc.recreate());
        bsc.startNotify();
    }
}
