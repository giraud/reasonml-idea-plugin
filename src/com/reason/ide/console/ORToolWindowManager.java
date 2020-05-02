package com.reason.ide.console;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.reason.ide.ORProjectManager;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;

public class ORToolWindowManager {

    private final Project project;

    private final ORToolWindowProvider toolWindowProvider;

    public static ORToolWindowManager getInstance(@NotNull Project project) {
        return new ORToolWindowManager(project);
    }

    private ORToolWindowManager(@NotNull Project project) {
        this.project = project;
        this.toolWindowProvider = ORToolWindowProvider.getInstance(project);
    }

    public void showToolWindows() {
        if (shouldShowBsToolWindow(project)) {
            ToolWindow bsToolWindow = toolWindowProvider.getBsToolWindow();
            bsToolWindow.setAvailable(true, null);
        }
        if (shouldShowDuneToolWindow(project)) {
            ToolWindow duneToolWindow = toolWindowProvider.getDuneToolWindow();
            duneToolWindow.setAvailable(true, null);
        }
        if (shouldShowEsyToolWindow(project)) {
            ToolWindow esyToolWindow = toolWindowProvider.getEsyToolWindow();
            esyToolWindow.setAvailable(true, null);
        }
    }

    private static boolean shouldShowBsToolWindow(@NotNull Project project) {
        return ORProjectManager.isBsProject(project);
    }

    private static boolean shouldShowDuneToolWindow(@NotNull Project project) {
        // if config is present, show the tool window
        if (ORProjectManager.isDuneProject(project)) {
            return true;
        }
        // if dune facet explicitly setup, also show the window
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        for (Module module : modules) {
            FacetManager instance = FacetManager.getInstance(module);
            DuneFacet duneFacet = instance.getFacetByType(DuneFacet.ID);
            if (duneFacet != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldShowEsyToolWindow(@NotNull Project project) {
        return ORProjectManager.isEsyProject(project);
    }
}
