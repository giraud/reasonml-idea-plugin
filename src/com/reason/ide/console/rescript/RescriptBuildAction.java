package com.reason.ide.console.rescript;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.comp.rescript.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class RescriptBuildAction extends CompilerAction {
    private final RescriptConsoleView myConsoleView;

    public RescriptBuildAction(RescriptConsoleView consoleView) {
        super("Build", "Build", AllIcons.Actions.Compile);
        myConsoleView = consoleView;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Compiler compiler = project == null ? null : project.getService(ResCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(!compiler.isRunning());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Compiler compiler = project == null ? null : project.getService(ResCompiler.class);
        if (compiler != null) {
            doAction(project, CliType.Rescript.MAKE, () -> e.getPresentation().setEnabled(!compiler.isRunning()));
        }
    }
}
