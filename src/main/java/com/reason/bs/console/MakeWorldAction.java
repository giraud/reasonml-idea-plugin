package com.reason.bs.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptManager;
import com.reason.bs.compiler.BsCompiler;

import static com.reason.bs.compiler.CliType.cleanMake;

public class MakeWorldAction extends DumbAwareAction {

    private final ConsoleView m_console;
    private final Project m_project;

    MakeWorldAction(ConsoleView console, Project project) {
        super("Clean and make world", "Clean and make world", AllIcons.General.Web);
        m_project = project;
        m_console = console;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Bucklescript bucklescript = BucklescriptManager.getInstance(m_project);
        BsCompiler bsc = bucklescript.getOrCreateCompiler();
        if (bsc != null) {
            ProcessHandler bscProcess = bsc.recreate(cleanMake);
            if (bscProcess != null) {
                m_console.attachToProcess(bscProcess);
                bsc.startNotify();
            }
        }
    }
}
