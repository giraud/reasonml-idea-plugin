package com.reason.build.dune;

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
import com.reason.build.bs.ModuleConfiguration;
import com.reason.build.bs.compiler.CliType;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DuneManager implements Compiler, ProjectComponent {

    private final Project m_project;

    @Nullable
    private DuneCompiler m_compiler;

    @NotNull
    public static Compiler getInstance(@NotNull Project project) {
        return project.getComponent(DuneManager.class);
    }

    private DuneManager(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public void projectOpened() {
        VirtualFile baseDir = Platform.findBaseRoot(m_project);
        VirtualFile duneConfig = baseDir.findChild("jbuild");

        if (duneConfig != null) {
            ModuleConfiguration moduleConfiguration = new ModuleConfiguration(m_project);
            m_compiler = new DuneCompiler(moduleConfiguration);
        }
    }

    @Override
    public void refresh(@NotNull VirtualFile bsconfigFile) {
        // Nothing to do
    }

    @Override
    public void run(@NotNull VirtualFile file) {
        if (m_compiler != null && FileHelper.isCompilable(file.getFileType())) {
            if (m_compiler.start()) {
                ProcessHandler recreate = m_compiler.recreate();
                if (recreate != null) {
                    getBsbConsole().attachToProcess(recreate);
                    m_compiler.startNotify();
                } else {
                    m_compiler.terminated();
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
