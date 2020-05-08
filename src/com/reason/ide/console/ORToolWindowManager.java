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

    public void showHideToolWindows() {
        ToolWindow bsToolWindow = toolWindowProvider.getBsToolWindow();
        ToolWindow duneToolWindow = toolWindowProvider.getDuneToolWindow();
        ToolWindow esyToolWindow = toolWindowProvider.getEsyToolWindow();
        bsToolWindow.setAvailable(shouldShowBsToolWindow(project), null);
        duneToolWindow.setAvailable(shouldShowDuneToolWindow(project), null);
        esyToolWindow.setAvailable(shouldShowEsyToolWindow(project), null);
    }

    private static boolean shouldShowBsToolWindow(@NotNull Project project) {
        return ORProjectManager.isBsProject(project);
    }

    private static boolean shouldShowDuneToolWindow(@NotNull Project project) {
        ORCompilerManager compilerManager = ServiceManager.getService(project, ORCompilerManager.class);
        Optional<Compiler> duneCompiler = compilerManager.getCompiler(CompilerType.DUNE);
        return ORProjectManager.isDuneProject(project) && duneCompiler.isPresent();
    }

    private static boolean shouldShowEsyToolWindow(@NotNull Project project) {
        return ORProjectManager.isEsyProject(project);
    }
}
