package com.reason.build.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class MakeAction extends CompilerAction {

    public MakeAction() {
        super("Make", "Make", AllIcons.Actions.Compile);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.make);
        }
    }
}
