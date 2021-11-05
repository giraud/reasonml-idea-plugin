package com.reason.ide.console.rescript;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.comp.rescript.*;
import com.reason.ide.console.*;
import icons.*;
import org.jetbrains.annotations.*;

public class RescriptResetAction extends CompilerAction {
    public RescriptResetAction() {
        super("Clean", "Clean", ORIcons.RESET);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        ResCompiler compiler = project == null ? null : project.getService(ResCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(!compiler.isRunning());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Rescript.CLEAN, null);
        }
    }
}
