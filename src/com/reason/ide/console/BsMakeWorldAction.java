package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BsMakeWorldAction extends CompilerAction {

    @SuppressWarnings("WeakerAccess")
    public BsMakeWorldAction() {
        super("Clean and make world bsb", "Clean and make world bsb", AllIcons.General.Web);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Bs.CLEAN_MAKE);
        }
    }
}
