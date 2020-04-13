package com.reason.dune;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.Compiler;
import com.reason.CompilerProcess;
import com.reason.Platform;
import com.reason.ProcessFinishedListener;
import com.reason.esy.EsyProcess;
import com.reason.hints.InsightManager;
import com.reason.ide.console.CliType;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneCompiler implements Compiler {

    @NotNull
    private final Project m_project;

    public static Compiler getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, DuneCompiler.class);
    }

    DuneCompiler(@NotNull Project project) {
        m_project = project;
    }

    @Nullable
    @Override
    public VirtualFile findContentRoot(@NotNull Project project) {
        return Platform.findORDuneContentRoot(project);
    }

    @Override
    public void refresh(@NotNull VirtualFile configFile) {
        // Nothing to do
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        if (cliType == CliType.cleanMake) {
            run(file, CliType.clean, () ->
                    run(file, CliType.make, onProcessTerminated));
        } else {
            CompilerProcess process = isEsyFacetConfigured()
                ? EsyProcess.getInstance(m_project)
                : DuneProcess.getInstance(m_project);
            if (process.start()) {
                ProcessHandler duneHandler = process.recreate(cliType, onProcessTerminated);
                if (duneHandler != null) {
                    ConsoleView console = getConsoleView();
                    if (console != null) {
                        long start = System.currentTimeMillis();
                        console.attachToProcess(duneHandler);
                        duneHandler.addProcessListener(new ProcessFinishedListener(start));
                    }
                    process.startNotify();
                    ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(file);
                } else {
                    process.terminate();
                }
            }
        }
    }

    public boolean isEsyFacetConfigured() {
        ModuleManager moduleManager = ModuleManager.getInstance(m_project);
        for (Module module : moduleManager.getModules()) {
            FacetManager instance = FacetManager.getInstance(module);
            DuneFacet duneFacet = instance.getFacetByType(DuneFacet.ID);
            if (duneFacet != null && duneFacet.getConfiguration().isEsy) {
                return true;
            }
        }
        return false;
    }

    // copied
    @Nullable
    private ConsoleView getConsoleView() {
        ConsoleView console = null;

        ToolWindow window = ToolWindowManager.getInstance(m_project).getToolWindow("Bucklescript");
        Content windowContent = window.getContentManager().getContent(0);
        if (windowContent != null) {
            SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
            JComponent panelComponent = component.getComponent();
            if (panelComponent != null) {
                console = (ConsoleView) panelComponent.getComponent(0);
            }
        }

        return console;
    }
}
