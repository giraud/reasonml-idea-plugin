package com.reason.build.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class MakeWorldAction extends CompilerAction {

    MakeWorldAction(ConsoleView console, Project project) {
        super("Clean and make world", "Clean and make world", AllIcons.General.Web, console, project);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        doAction(CliType.cleanMake);
    }
}
