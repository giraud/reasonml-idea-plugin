package com.reason.ide.console.esy;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reason.comp.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.NotNull;

public class EsyBuildAction extends CompilerAction {

  public EsyBuildAction() {
    super("Build esy", "Build esy", AllIcons.Actions.Compile);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      doAction(project, CliType.Esy.BUILD);
    }
  }
}
