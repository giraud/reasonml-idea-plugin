package com.reason.ide.console;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.reason.Compiler;
import com.reason.CompilerType;
import com.reason.ORCompilerManager;
import com.reason.ide.ORProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
        ORCompilerManager compilerManager = ServiceManager.getService(project, ORCompilerManager.class);
        Optional<Compiler> duneCompiler = compilerManager.getCompiler(CompilerType.DUNE);
        return duneCompiler.isPresent() && ORProjectManager.isDuneProject(project);
    }

    private static boolean shouldShowEsyToolWindow(@NotNull Project project) {
        return ORProjectManager.isEsyProject(project);
    }
}
