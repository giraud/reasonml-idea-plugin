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
import com.intellij.ui.content.Content;
import com.reason.Compiler;
import com.reason.*;
import com.reason.hints.InsightManager;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;
import com.reason.ide.console.ORToolWindowProvider;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;
import java.util.Set;

public class DuneCompiler implements Compiler {

    private static final Log LOG = Log.create("compiler.dune");

    @NotNull
    private final Project project;

    DuneCompiler(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public CompilerType getType() {
        return CompilerType.DUNE;
    }

//    Sdk odk = duneFacet.getODK();
//                if (odk == null) {
//        SHOW_OCAML_SDK_NOT_FOUND.run();
//        return false;
//    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            DuneFacet duneFacet = FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
            if (duneFacet != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<VirtualFile> findFirstContentRoot(@NotNull Project project) {
        return ORProjectManager.findFirstDuneContentRoot(project);
    }

    @Override
    public Set<VirtualFile> findContentRoots(@NotNull Project project) {
        return ORProjectManager.findDuneContentRoots(project);
    }

    @Override
    public void refresh(@NotNull VirtualFile configFile) {
        // Nothing to do
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated) {
        run(file, CliType.Dune.BUILD, onProcessTerminated);
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        if (!(cliType instanceof CliType.Dune)) {
            LOG.error("Invalid cliType for dune compiler. cliType = " + cliType);
            return;
        }
        CompilerProcess process = DuneProcess.getInstance(project);
        if (process.start()) {
            ProcessHandler duneHandler = process.recreate(cliType, onProcessTerminated);
            if (duneHandler != null) {
                ConsoleView console = getConsoleView();
                if (console != null) {
                    console.attachToProcess(duneHandler);
                    duneHandler.addProcessListener(new ProcessFinishedListener());
                }
                process.startNotify();
                ServiceManager.getService(project, InsightManager.class).downloadRincewindIfNeeded(file);
            } else {
                process.terminate();
            }
        }
    }

    @Nullable
    @Override
    public ConsoleView getConsoleView() {
        ORToolWindowProvider windowProvider = ORToolWindowProvider.getInstance(project);
        ToolWindow duneToolWindow = windowProvider.getDuneToolWindow();
        Content windowContent = duneToolWindow.getContentManager().getContent(0);
        if (windowContent == null) {
            return null;
        }
        SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
        JComponent panelComponent = component.getComponent();
        if (panelComponent == null) {
            return null;
        }
        return (ConsoleView) panelComponent.getComponent(0);
    }
}
