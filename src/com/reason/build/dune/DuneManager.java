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
import com.reason.Platform;
import com.reason.build.Compiler;
import com.reason.build.console.CliType;
import com.reason.hints.InsightManagerImpl;
import com.reason.icons.Icons;
import com.reason.ide.files.FileHelper;

public class DuneManager implements Compiler, ProjectComponent {

    private final Project m_project;

    @NotNull
    public static Compiler getInstance(@NotNull Project project) {
        return project.getComponent(DuneManager.class);
    }

    DuneManager(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public void refresh(@NotNull VirtualFile bsconfigFile) {
        // Nothing to do
    }

    @Override
    public void run(@NotNull VirtualFile file) {
        if (FileHelper.isCompilable(file.getFileType())) {
            VirtualFile duneConfig = Platform.findBaseRoot(m_project).findChild("jbuild");
            if (duneConfig != null) {
                DuneProcess process = DuneProcess.getInstance(m_project);
                if (process.start()) {
                    ProcessHandler handler = process.recreate();
                    if (handler != null) {
                        getBsbConsole().attachToProcess(handler);
                        process.startNotify();
                        InsightManagerImpl.getInstance(m_project).downloadRincewindIfNeeded();
                    } else {
                        process.terminated();
                    }
                }
            }
        }
    }

    @Override
    public void run(@NotNull VirtualFile file, @NotNull CliType cliType) {
        run(file, CliType.standard);
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

    //region Compatibility
    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }
    //endregion
}
