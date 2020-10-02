package com.reason.ide;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.reason.Log;
import com.reason.ide.console.ORToolWindowManager;
import org.jetbrains.annotations.NotNull;

public class ORPostStartupActivity implements StartupActivity, DumbAware {

  private static final Log LOG = Log.create("activity.startup");

  @Override
  public void runActivity(@NotNull Project project) {
    ORProjectRootListener.ensureSubscribed(project);
    ORFacetListener.ensureSubscribed(project);
    ORFileDocumentListener.ensureSubscribed(project);
    LOG.debug("Subscribed project and document listeners.");
    showToolWindowsLater(project);
  }

  /* show tool windows after indexing finishes */
  private static void showToolWindowsLater(Project project) {
    ORToolWindowManager toolWindowManager = ORToolWindowManager.getInstance(project);
    DumbService.getInstance(project).smartInvokeLater(toolWindowManager::showHideToolWindows);
  }
}
