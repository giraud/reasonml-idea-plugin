package com.reason.build.bs;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.Platform;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.build.bs.compiler.CliType;
import com.reason.build.bs.refmt.RefmtProcess;
import com.reason.ide.RmlNotification;
import com.reason.ide.files.FileHelper;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class BucklescriptManager implements Bucklescript, ProjectComponent {

    private final Project m_project;

    private boolean m_disabled;
    private final Map<String, BsConfig> m_configs = new THashMap<>();
    @Nullable
    private BsCompiler m_compiler;

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
        m_disabled = Boolean.getBoolean("reasonBsbDisabled");
        if (m_disabled) {
            // But you should NEVER do that
            Notifications.Bus.notify(new RmlNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            return;
        }

        ModuleConfiguration moduleConfiguration = new ModuleConfiguration(m_project);
        m_compiler = new BsCompiler(moduleConfiguration);
    }

    @Override
    public void projectClosed() {
        if (m_compiler != null) {
            m_compiler.killIt();
        }
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
    public String getNamespace(@NotNull VirtualFile sourceFile) {
        VirtualFile bsConfigFile = Platform.findBsConfigFromFile(m_project, sourceFile);
        if (bsConfigFile != null) {
            BsConfig bsConfig = getOrRefreshBsConfig(bsConfigFile);
            return bsConfig.getNamespace();
        }
        return "";
    }

    //region Compiler
    @Override
    public void refresh(@NotNull VirtualFile bsconfigFile) {
        BsConfig updatedConfig = BsConfig.read(bsconfigFile);
        m_configs.put(bsconfigFile.getCanonicalPath(), updatedConfig);
    }

    @Override
    public void run(@NotNull VirtualFile sourceFile) {
        if (!m_disabled && m_compiler != null && FileHelper.isCompilable(sourceFile.getFileType())) {
            VirtualFile bsConfigFile = Platform.findBsConfigFromFile(m_project, sourceFile);
            if (bsConfigFile != null) {
                getOrRefreshBsConfig(bsConfigFile);

                if (m_compiler.start()) {
                    ProcessHandler recreate = m_compiler.recreate(sourceFile, CliType.standard);
                    if (recreate != null) {
                        getBsbConsole().attachToProcess(recreate);
                        m_compiler.startNotify();
                    } else {
                        m_compiler.terminated();
                    }
                }
            }
        }
    }

    @NotNull
    private BsConfig getOrRefreshBsConfig(@NotNull VirtualFile bsConfigFile) {
        String bsConfigPath = bsConfigFile.getCanonicalPath();
        BsConfig bsConfig = m_configs.get(bsConfigPath);
        if (bsConfig == null) {
            refresh(bsConfigFile);
            bsConfig = m_configs.get(bsConfigPath);
        }
        return bsConfig;
    }
    //endregion

    @Override
    public boolean isDependency(@Nullable VirtualFile file) {
        if (file == null) {
            return false;
        }

        VirtualFile bsConfigFile = Platform.findBsConfigFromFile(m_project, file);
        return bsConfigFile == null || getOrRefreshBsConfig(bsConfigFile).accept(file.getPath());
    }

    @Override
    public void convert(@NotNull VirtualFile virtualFile, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        String oldText = document.getText();
        String newText = refmt.convert(virtualFile, fromFormat, toFormat, oldText);
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

    @Override
    public void refmt(@NotNull VirtualFile sourceFile, @NotNull String format, @NotNull Document document) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        String oldText = document.getText();
        if (!oldText.isEmpty()) {
            String newText = refmt.run(sourceFile, format, oldText);
            if (!newText.isEmpty() && !oldText.equals(newText)) { // additional protection
                getApplication().runWriteAction(
                        () -> CommandProcessor.getInstance().executeCommand(m_project, () -> document.setText(newText), "reason.refmt", "CodeFormatGroup"));
            }
        }
    }

    @Override
    public boolean isRefmtOnSaveEnabled() {
        return RefmtProcess.getInstance(m_project).isOnSaveEnabled();
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
