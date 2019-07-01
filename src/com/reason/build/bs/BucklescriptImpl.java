package com.reason.build.bs;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.Platform;
import com.reason.build.bs.compiler.BsProcess;
import com.reason.build.bs.refmt.RefmtProcess;
import com.reason.build.console.CliType;
import com.reason.hints.InsightManager;
import com.reason.ide.ORNotification;
import com.reason.ide.settings.ReasonSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class BucklescriptImpl implements Bucklescript {

    private final Project m_project;
    private final Map<String, BsConfig> m_configs = new THashMap<>();

    private Boolean m_disabled = null; // Never call directly, use isDisabled()

    private BucklescriptImpl(@NotNull Project project) {
        m_project = project;
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
    public void refresh(@NotNull VirtualFile bsConfigFile) {
        BsConfig updatedConfig = BsConfig.read(m_project, bsConfigFile);
        m_configs.put(bsConfigFile.getCanonicalPath(), updatedConfig);
    }

    @Override
    public void run(@NotNull VirtualFile sourceFile, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated) {
        if (!isDisabled() && ReasonSettings.getInstance(m_project).isEnabled()) {
            VirtualFile bsConfigFile = Platform.findBsConfigFromFile(m_project, sourceFile);
            if (bsConfigFile != null) {
                getOrRefreshBsConfig(bsConfigFile);
                BsProcess process = ServiceManager.getService(m_project, BsProcess.class);
                if (process.start()) {
                    ProcessHandler bscProcess = process.recreate(sourceFile, cliType, onProcessTerminated);
                    if (bscProcess != null) {
                        ConsoleView console = getBsbConsole();
                        if (console != null) {
                            console.attachToProcess(bscProcess);
                        }
                        process.startNotify();
                        ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(sourceFile);
                    } else {
                        process.terminated();
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
    public void convert(@NotNull VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        String oldText = document.getText();
        String newText = refmt.convert(virtualFile, isInterface, fromFormat, toFormat, oldText);
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
    public void refmt(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String format, @NotNull Document document) {
        if (ReasonSettings.getInstance(m_project).isEnabled()) {
            RefmtProcess refmt = RefmtProcess.getInstance(m_project);
            String oldText = document.getText();
            if (!oldText.isEmpty()) {
                String newText = refmt.run(sourceFile, isInterface, format, oldText);
                if (!newText.isEmpty() && !oldText.equals(newText)) { // additional protection
                    getApplication().runWriteAction(
                            () -> CommandProcessor.getInstance().executeCommand(m_project, () -> document.setText(newText), "reason.refmt", "CodeFormatGroup"));
                }
            }
        }
    }

    @Nullable
    public ConsoleView getBsbConsole() {
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

    private boolean isDisabled() {
        if (m_disabled == null) {
            m_disabled = Boolean.getBoolean("reasonBsbDisabled");
            if (m_disabled) {
                // But you should NEVER do that
                Notifications.Bus.notify(new ORNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            }
        }

        return m_disabled;
    }
}
