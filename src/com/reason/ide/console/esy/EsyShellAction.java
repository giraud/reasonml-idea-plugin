package com.reason.ide.console.esy;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class EsyShellAction extends CompilerAction {

    public EsyShellAction() {
        super("Shell esy", "Shell esy", AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Esy.SHELL, null);
        }
    }
}
