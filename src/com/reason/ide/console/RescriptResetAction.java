package com.reason.ide.console;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import icons.*;
import org.jetbrains.annotations.*;

public class RescriptResetAction extends CompilerAction {
    public RescriptResetAction() {
        super("Clean", "Clean", ORIcons.RESET);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Rescript.CLEAN);
        }
    }
}
