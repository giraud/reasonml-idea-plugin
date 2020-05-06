package com.reason.esy;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.reason.Compiler;
import com.reason.CompilerType;
import com.reason.Log;
import com.reason.ProcessFinishedListener;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;
import com.reason.ide.console.ORToolWindowProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;
import java.util.Set;

public class EsyCompiler implements Compiler {

    private static final Log LOG = Log.create("compiler.esy");

    @NotNull
    private final Project m_project;

    EsyCompiler(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public Optional<VirtualFile> findFirstContentRoot(@NotNull Project project) {
        return ORProjectManager.findFirstEsyContentRoot(project);
    }

    @Override
    public Set<VirtualFile> findContentRoots(@NotNull Project project) {
        return ORProjectManager.findEsyContentRoots(project);
    }

    @Override
    public void refresh(@NotNull VirtualFile configFile) {
        // Nothing to do
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        run(file, CliType.Esy.BUILD, onProcessTerminated);
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated) {
        if (!(cliType instanceof CliType.Esy)) {
            LOG.error("Invalid cliType for esy compiler. cliType = " + cliType);
            return;
        }
        EsyProcess process = EsyProcess.getInstance(m_project);
        ProcessHandler processHandler = process.recreate(cliType, onProcessTerminated);
        if (processHandler != null) {
            processHandler.addProcessListener(new ProcessFinishedListener());
            ConsoleView console = getConsoleView();
            console.attachToProcess(processHandler);
            process.startNotify();
        }
    }

    @Override
    public CompilerType getType() {
        return CompilerType.ESY;
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        // Esy compiler doesn't require any project-level configuration
        return true;
    }

    @Override
    public ConsoleView getConsoleView() {
        ORToolWindowProvider windowProvider = ORToolWindowProvider.getInstance(m_project);
        ToolWindow esyToolWindow = windowProvider.getEsyToolWindow();
        Content windowContent = esyToolWindow.getContentManager().getContent(0);
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