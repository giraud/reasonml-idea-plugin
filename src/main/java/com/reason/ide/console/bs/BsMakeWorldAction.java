package com.reason.ide.console.bs;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public class BsMakeWorldAction extends CompilerAction {
    public BsMakeWorldAction() {
        super("Clean and make world bsb", "Clean and make world bsb", AllIcons.General.Web);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        BsCompiler compiler = project == null ? null : project.getService(BsCompiler.class);
        if (compiler != null) {
            e.getPresentation().setEnabled(!compiler.isRunning());
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        BsCompiler compiler = project == null ? null : project.getService(BsCompiler.class);
        if (compiler != null) {
            doAction(project, CliType.Bs.CLEAN_MAKE, (_void) -> e.getPresentation().setEnabled(!compiler.isRunning()));
        }
    }
}
