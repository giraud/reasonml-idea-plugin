package com.reason.ide.console.esy;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class EsyInstallAction extends CompilerAction {

    public EsyInstallAction() {
        super("Install esy", "Install esy", AllIcons.Actions.Install);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            doAction(project, CliType.Esy.INSTALL, null);
        }
    }
}
