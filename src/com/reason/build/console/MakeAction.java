package com.reason.build.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MakeAction extends CompilerAction {

    public MakeAction() {
        super("Make", "Make", AllIcons.Actions.Compile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.make);
        }
    }
}
