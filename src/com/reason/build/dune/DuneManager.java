package com.reason.build.dune;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.Icons;
import com.reason.build.Compiler;
import com.reason.build.console.CliType;
import com.reason.hints.InsightManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneManager implements Compiler {

    @NotNull
    private final Project m_project;

    DuneManager(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public void refresh(@NotNull VirtualFile bsconfigFile) {
        // Nothing to do
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        DuneProcess process = ServiceManager.getService(m_project, DuneProcess.class);
        if (process.start()) {
            ProcessHandler handler = process.recreate();
            if (handler != null) {
                ConsoleView console = getBsbConsole();
                if (console != null) {
                    console.attachToProcess(handler);
                }
                process.startNotify();
                ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(file);
            } else {
                process.terminated();
            }
        }
    }

    // copied
    @Nullable
    private ConsoleView getBsbConsole() {
        ConsoleView console = null;

        ToolWindow window = ToolWindowManager.getInstance(m_project).getToolWindow("Bucklescript");
        window.setIcon(Icons.DUNE_FILE);
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
