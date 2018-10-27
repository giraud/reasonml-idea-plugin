package com.reason.build.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class MakeWorldAction extends CompilerAction {

    MakeWorldAction() {
        super("Clean and make world", "Clean and make world", AllIcons.General.Web);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.cleanMake);
        }
    }
}
