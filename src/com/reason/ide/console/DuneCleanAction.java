package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public class DuneCleanAction extends CompilerAction {

    public DuneCleanAction() {
        super("Clean", "Clean", AllIcons.Actions.CloseHovered);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Dune.CLEAN);
        }
    }

    @Override
    public CompilerType getCompilerType() {
        return CompilerType.DUNE;
    }
}
