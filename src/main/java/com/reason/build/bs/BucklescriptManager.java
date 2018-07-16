package com.reason.build.bs;

import java.io.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.reason.Platform;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.build.bs.compiler.CliType;
import com.reason.build.bs.refmt.RefmtProcess;
import com.reason.ide.RmlNotification;
import com.reason.ide.files.FileHelper;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class BucklescriptManager implements Bucklescript, ProjectComponent {

    private final Project m_project;

    @Nullable
    private BsConfig m_config;
    @Nullable
    private BsCompiler m_compiler;
    @Nullable
    private RefmtProcess m_refmt;

    private BucklescriptManager(Project project) {
        m_project = project;
    }

    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }

    /**
     * Returns the bucklescript instance for the specified project.
     *
     * @param project the project for which the bucklescript is requested.
     * @return the bucklescript instance.
     */
    public static Bucklescript getInstance(Project project) {
        return project.getComponent(Bucklescript.class);
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "reason.bucklescript";
    }

    @Override
    public void projectOpened() {
        VirtualFile baseDir = Platform.findBaseRoot(m_project);
        VirtualFile bsconfig = baseDir.findChild("bsconfig.json");

        boolean disabled = Boolean.getBoolean("reasonBsbDisabled");
        if (disabled) {
            // But you should NEVER do that
            Notifications.Bus.notify(new RmlNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            return;
        }

        if (bsconfig != null) {
            ModuleConfiguration moduleConfiguration = new ModuleConfiguration(m_project);
            m_config = BsConfig.read(bsconfig);
            m_compiler = new BsCompiler(moduleConfiguration);
            m_refmt = new RefmtProcess(moduleConfiguration);
        }
    }

    @Override
    public void projectClosed() {
        if (m_compiler != null) {
            m_compiler.killIt();
        }
        m_config = null;
        m_compiler = null;
    }

    @Nullable
    @Override
    public BsCompiler getCompiler() {
        return m_compiler;
    }

    @Nullable
    @Override
    public BsCompiler getOrCreateCompiler() {
        if (m_compiler == null) {
            // Try again
            projectOpened();
        }

        return m_compiler;
    }

    @NotNull
    @Override
    public String getNamespace() {
        return m_config == null ? "" : m_config.getNamespace();
    }

    @Override
    public void run(FileType fileType) {
        if (m_compiler != null && FileHelper.isCompilable(fileType)) {
            if (m_compiler.start()) {
                ProcessHandler recreate = m_compiler.recreate(CliType.standard);
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
    public boolean isDependency(@Nullable String path) {
        return m_config == null || m_config.accept(path);
    }

    @Override
    public boolean isDependency(@Nullable PsiFile file) {
        return file != null && (m_config == null || m_config.accept(file.getVirtualFile().getCanonicalPath()));
    }

    @Override
    public void refresh() {
        VirtualFile bsconfig = Platform.findBaseRoot(m_project).findChild("bsconfig.json");
        if (bsconfig != null) {
            m_config = BsConfig.read(bsconfig);
        }
    }

    @Override
    public void convert(@NotNull VirtualFile virtualFile, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document) {
        if (m_refmt != null) {
            String oldText = document.getText();
            String newText = m_refmt.convert(fromFormat, toFormat, oldText);
            if (!oldText.isEmpty() && !newText.isEmpty()) { // additional protection
                getApplication().runWriteAction(() -> {
                    CommandProcessor.getInstance().executeCommand(m_project, () -> document.setText(newText), "reason.refmt", "CodeFormatGroup");
                    try {
                        virtualFile.rename(this, virtualFile.getNameWithoutExtension() + "." + toFormat);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @Override
    public void refmt(@NotNull String format, @NotNull Document document) {
        if (m_refmt != null) {
            String oldText = document.getText();
            String newText = m_refmt.run(format, oldText);
            if (!oldText.isEmpty() && !newText.isEmpty() && !oldText.equals(newText)) { // additional protection
                getApplication().runWriteAction(
                        () -> CommandProcessor.getInstance().executeCommand(m_project, () -> document.setText(newText), "reason.refmt", "CodeFormatGroup"));
            }
        }
    }

    @Override
    public boolean isRefmtOnSaveEnabled() {
        return m_refmt != null && m_refmt.isOnSaveEnabled();
    }

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
