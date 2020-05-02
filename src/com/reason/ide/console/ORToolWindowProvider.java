package com.reason.ide.console;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import org.jetbrains.annotations.NotNull;

public class ORToolWindowProvider {

    private final ToolWindowManagerEx toolWindowManager;

    public static ORToolWindowProvider getInstance(@NotNull Project project) {
        return new ORToolWindowProvider(project);
    }

    private ORToolWindowProvider(@NotNull Project project) {
        this.toolWindowManager = ToolWindowManagerEx.getInstanceEx(project);
    }

    public ToolWindow getBsToolWindow() {
        return getToolWindow(BsToolWindowFactory.ID);
    }

    public ToolWindow getDuneToolWindow() {
        return getToolWindow(DuneToolWindowFactory.ID);
    }

    public ToolWindow getEsyToolWindow() {
        return getToolWindow(EsyToolWindowFactory.ID);
    }

    private ToolWindow getToolWindow(String id) {
        return toolWindowManager.getToolWindow(id);
    }
}
