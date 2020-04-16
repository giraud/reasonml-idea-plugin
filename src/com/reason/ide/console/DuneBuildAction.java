package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public class DuneBuildAction extends CompilerAction {

    public DuneBuildAction() {
        super("Build", "Build", AllIcons.Actions.Compile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Dune.BUILD);
        }
    }

    @Override
    public CompilerType getCompilerType() {
        return CompilerType.DUNE;
    }
}
