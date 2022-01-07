package com.reason.ide.console.dune;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.comp.dune.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class DuneBuildAction extends CompilerAction {
    public DuneBuildAction() {
        super("Build dune", "Build dune", AllIcons.Actions.Compile);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Compiler compiler = project == null ? null : project.getService(DuneCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(!compiler.isRunning());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Compiler compiler = project == null ? null : project.getService(DuneCompiler.class);
        if (compiler != null) {
            doAction(project, CliType.Dune.BUILD, (_void) -> e.getPresentation().setEnabled(!compiler.isRunning()));
        }
    }
}
