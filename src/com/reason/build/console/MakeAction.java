package com.reason.build.console;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MakeAction extends CompilerAction {

    MakeAction(@NotNull ConsoleView console, @NotNull Project project) {
        super("Make", "Make", AllIcons.Actions.Compile, console, project);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        doAction(CliType.make);
    }
}
