package com.reason.ide.console.dune;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.comp.dune.*;
import com.reason.ide.console.*;
import icons.*;
import org.jetbrains.annotations.*;

public class DuneCleanAction extends CompilerAction {
    public DuneCleanAction() {
        super("Clean dune", "Clean dune", ORIcons.RESET);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        DuneCompiler compiler = project == null ? null : project.getService(DuneCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(!compiler.isRunning());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Dune.CLEAN, null);
        }
    }
}
