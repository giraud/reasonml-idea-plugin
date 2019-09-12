package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MakeWorldAction extends CompilerAction {

    @SuppressWarnings("WeakerAccess")
    public MakeWorldAction() {
        super("Clean and make world", "Clean and make world", AllIcons.General.Web);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.cleanMake);
        }
    }
}
