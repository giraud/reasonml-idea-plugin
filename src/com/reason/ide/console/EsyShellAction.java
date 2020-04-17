package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public class EsyShellAction extends CompilerAction {

    public EsyShellAction() {
        super("Shell", "Shell", AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Esy.SHELL);
        }
    }

    @Override
    public CompilerType getCompilerType() {
        return CompilerType.ESY;
    }
}
