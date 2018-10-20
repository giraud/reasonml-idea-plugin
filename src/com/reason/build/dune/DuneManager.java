package com.reason.build.dune;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.build.Compiler;
import com.reason.build.console.CliType;
import com.reason.icons.Icons;
import com.reason.ide.files.FileHelper;

public class DuneManager implements Compiler, ProjectComponent {

    private final Project m_project;
    private final DuneProcess m_duneProcess;

    @NotNull
    public static Compiler getInstance(@NotNull Project project) {
        return project.getComponent(DuneManager.class);
    }

    DuneManager(@NotNull Project project) {
        m_project = project;
        m_duneProcess = new DuneProcess(m_project);
    }

    @Override
    public void refresh(@NotNull VirtualFile bsconfigFile) {
        // Nothing to do
    }

    @Override
    public void run(@NotNull VirtualFile file) {
        if (FileHelper.isCompilable(file.getFileType())) {
            if (m_duneProcess.start()) {
                ProcessHandler recreate = m_duneProcess.recreate();
                if (recreate != null) {
                    getBsbConsole().attachToProcess(recreate);
                    m_duneProcess.startNotify();
                } else {
                    m_duneProcess.terminated();
                }
            }
        }
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType) {
        run(file);
    }

    // copied
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

    @Override
    public String toString() {
        return "Dune";
    }
}
