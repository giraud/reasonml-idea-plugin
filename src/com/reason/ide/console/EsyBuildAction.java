package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public class EsyBuildAction extends CompilerAction {

    public EsyBuildAction() {
        super("Build", "Build", AllIcons.Actions.Compile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Esy.BUILD);
        }
    }

    @Override
    public CompilerType getCompilerType() {
        return CompilerType.ESY;
    }
}
