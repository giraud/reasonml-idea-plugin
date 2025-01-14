package com.reason.ide.console.esy;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.comp.esy.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class EsyBuildAction extends CompilerAction {
    public EsyBuildAction() {
        super("Build esy", "Build esy", AllIcons.Actions.Compile);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ORCompiler compiler = project == null ? null : project.getService(EsyCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(compiler.isAvailable());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ORCompiler compiler = project == null ? null : project.getService(EsyCompiler.class);
        if (compiler != null) {
            doAction(project, CliType.Esy.BUILD, (_void) -> e.getPresentation().setEnabled(compiler.isAvailable()));
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
