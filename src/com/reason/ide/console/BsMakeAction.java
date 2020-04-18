package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public class BsMakeAction extends CompilerAction {

    public BsMakeAction() {
        super("Make", "Make", AllIcons.Actions.Compile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Bs.MAKE);
        }
    }

    @Override
    public CompilerType getCompilerType() {
        return CompilerType.BS;
    }
}
