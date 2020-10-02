package com.reason.ide.console;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORToolWindowProvider {

  private final ToolWindowManager m_toolWindowManager;

  public static ORToolWindowProvider getInstance(@NotNull Project project) {
    return new ORToolWindowProvider(project);
  }

  private ORToolWindowProvider(@NotNull Project project) {
    m_toolWindowManager = ToolWindowManager.getInstance(project);
  }

  @Nullable
  public ToolWindow getBsToolWindow() {
    return getToolWindow(BsToolWindowFactory.ID);
  }

  @Nullable
  public ToolWindow getDuneToolWindow() {
    return getToolWindow(DuneToolWindowFactory.ID);
  }

  @Nullable
  public ToolWindow getEsyToolWindow() {
    return getToolWindow(EsyToolWindowFactory.ID);
  }

  @Nullable
  private ToolWindow getToolWindow(String id) {
    return m_toolWindowManager.getToolWindow(id);
  }
}
