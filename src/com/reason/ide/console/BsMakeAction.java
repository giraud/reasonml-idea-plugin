package com.reason.ide.console;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class BsMakeAction extends CompilerAction {

  public BsMakeAction() {
    super("Make bsb", "Make bsb", AllIcons.Actions.Compile);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      doAction(project, CliType.Bs.MAKE);
    }
  }
}
