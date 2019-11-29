package com.reason.ide;

import org.jetbrains.annotations.NotNull;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.reason.Icons;
import com.reason.ide.facet.DuneFacet;

public class ORPostStartupActivity implements StartupActivity, DumbAware {
    @Override
    public void runActivity(@NotNull Project project) {
        ToolWindow bucklescript = ToolWindowManagerEx.getInstanceEx(project).getToolWindow("Bucklescript");
        if (bucklescript != null) {
            ModuleManager moduleManager = ModuleManager.getInstance(project);
            Module[] modules = moduleManager.getModules();
            for (Module module : modules) {
                FacetManager instance = FacetManager.getInstance(module);
                DuneFacet duneFacet = instance.getFacetByType(DuneFacet.ID);
                if (duneFacet != null) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        boolean isEsy = duneFacet.getConfiguration().isEsy;
                        bucklescript.setIcon(isEsy ? Icons.ESY_TOOL : Icons.DUNE_TOOL);
                        bucklescript.setStripeTitle(isEsy ? "Esy" : "Dune");
                        bucklescript.setTitle("Process");
                    });
                    return;
                }
            }
            ApplicationManager.getApplication().invokeLater(() -> bucklescript.setIcon(Icons.BUCKLESCRIPT_TOOL));
        }
    }
}
