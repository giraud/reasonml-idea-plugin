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

    private final Project m_project;

    private final ORToolWindowProvider m_toolWindowProvider;

    public static ORToolWindowManager getInstance(@NotNull Project project) {
        return new ORToolWindowManager(project);
    }

    private ORToolWindowManager(@NotNull Project project) {
        m_project = project;
        m_toolWindowProvider = ORToolWindowProvider.getInstance(project);
    }

    public void showHideToolWindows() {
        ToolWindow bsToolWindow = m_toolWindowProvider.getBsToolWindow();
        if (bsToolWindow != null) {
            bsToolWindow.setAvailable(shouldShowBsToolWindow(m_project), null);
        }

        ToolWindow duneToolWindow = m_toolWindowProvider.getDuneToolWindow();
        if (duneToolWindow != null) {
            duneToolWindow.setAvailable(shouldShowDuneToolWindow(m_project), null);
        }

        ToolWindow esyToolWindow = m_toolWindowProvider.getEsyToolWindow();
        if (esyToolWindow != null) {
            esyToolWindow.setAvailable(shouldShowEsyToolWindow(m_project), null);
        }
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
