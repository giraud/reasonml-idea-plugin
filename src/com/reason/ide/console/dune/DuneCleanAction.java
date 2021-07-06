package com.reason.ide.console.dune;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class DuneCleanAction extends CompilerAction {
    public DuneCleanAction() {
        super("Clean dune", "Clean dune", AllIcons.Actions.CloseHovered);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Dune.CLEAN);
        }
    }
}
