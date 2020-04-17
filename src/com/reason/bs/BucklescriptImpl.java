package com.reason.bs;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoConstants;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reason.*;
import com.reason.hints.InsightManager;
import com.reason.ide.console.BsToolWindowFactory;
import com.reason.ide.console.CliType;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.coverage.gnu.trove.THashMap;

import javax.swing.*;
import java.util.Map;

public class BucklescriptImpl implements Bucklescript {

    @NotNull
    private final Project m_project;
    private final Map<String, BsConfig> m_configs = new THashMap<>();

    @Nullable
    private Boolean m_disabled = null; // Never call directly, use isDisabled()

    private BucklescriptImpl(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    @Override
    public String getNamespace(@NotNull VirtualFile sourceFile) {
        VirtualFile bsConfigFile = Platform.findAncestorBsconfig(m_project, sourceFile);
        if (bsConfigFile != null) {
            BsConfig bsConfig = getOrRefreshBsConfig(bsConfigFile);
            return bsConfig.getNamespace();
        }
        return "";
    }

    //region Compiler
    @Nullable
    @Override
    public VirtualFile findContentRoot(@NotNull Project project) {
        return Platform.findORPackageJsonContentRoot(project);
    }

    @Override
    public void refresh(@NotNull VirtualFile bsConfigFile) {
        BsConfig updatedConfig = BsConfigReader.read(bsConfigFile);
        m_configs.put(bsConfigFile.getCanonicalPath(), updatedConfig);
    }

    @Override
    public void run(@NotNull VirtualFile sourceFile, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated) {
        if (!isDisabled() && ReasonSettings.getInstance(m_project).isEnabled()) {
            VirtualFile bsconfigFile = Platform.findAncestorBsconfig(m_project, sourceFile);
            if (bsconfigFile != null) {
                getOrRefreshBsConfig(bsconfigFile);
                BsProcess process = ServiceManager.getService(m_project, BsProcess.class);
                if (process.start()) {
                    ProcessHandler bscHandler = process.recreate(sourceFile, cliType, onProcessTerminated);
                    if (bscHandler != null) {
                        ConsoleView console = getBsbConsole();
                        if (console != null) {
                            long start = System.currentTimeMillis();
                            console.attachToProcess(bscHandler);
                            bscHandler.addProcessListener(new ProcessFinishedListener(start));
                        }
                        process.startNotify();
                        ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(sourceFile);
                    } else {
                        process.terminate();
                    }
                }
            }
        }
    }

    @Override
    public CompilerType getType() {
        return CompilerType.BS;
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

        VirtualFile bsConfigFile = Platform.findAncestorBsconfig(m_project, file);
        return bsConfigFile == null || getOrRefreshBsConfig(bsConfigFile).accept(file.getPath());
    }

    @Override
    @Nullable
    public String convert(@NotNull VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat,
                          @NotNull Document document) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        String oldText = document.getText();
        String newText = refmt.convert(virtualFile, isInterface, fromFormat, toFormat, oldText);
        // additional protection
        return oldText.isEmpty() || newText.isEmpty() ? null : newText;
    }

    // Try externalFormatProcessor
    // see https://github.com/Mizzlr/intellij-community/blob/7e1217822045325b2e9269505d07c65daa9e5e9d/plugins/sh/src/com/intellij/sh/formatter/ShExternalFormatter.java
    @Override
    public void refmt(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String format, @NotNull Document document) {
        refmtCount(sourceFile, isInterface, format, document, 1);
    }

    public void refmtCount(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String format, @NotNull Document document, final int retries) {
        if (!sourceFile.exists()) {
            return;
        }

        if (ReasonSettings.getInstance(m_project).isEnabled()) {
            long before = document.getModificationStamp();

            RefmtProcess refmt = RefmtProcess.getInstance(m_project);
            String oldText = document.getText();
            if (!oldText.isEmpty()) {
                String newText = refmt.run(sourceFile, isInterface, format, oldText);
                if (!newText.isEmpty() && !oldText.equals(newText)) { // additional protection
                    ApplicationManager.getApplication().invokeLater(() -> {
                        long after = document.getModificationStamp();
                        if (after > before) {
                            // Document has changed, redo refmt one time
                            if (retries < 2) {
                                refmtCount(sourceFile, isInterface, format, document, retries + 1);
                            }
                        } else {
                            CommandProcessor.getInstance().executeCommand(m_project, () -> {
                                WriteAction.run(() -> {
                                    document.setText(newText);
                                    FileDocumentManager.getInstance().saveDocument(document);
                                });
                                sourceFile.putUserData(UndoConstants.FORCE_RECORD_UNDO, null);
                            }, "reason.refmt", "CodeFormatGroup", document);
                        }
                    });
                }
            }
        }
    }

    @Nullable
    public ConsoleView getBsbConsole() {
        ConsoleView console = null;

        ToolWindow window = ToolWindowManager.getInstance(m_project).getToolWindow(BsToolWindowFactory.ID);
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
    @NotNull
    public Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        String content = null;

        if (contentRoot != null) {
            VirtualFile ninja = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninja != null) {
                content = FileUtil.readFileContent(ninja);
            }
        }

        return new Ninja(content);
    }

    private boolean isDisabled() {
        if (m_disabled == null) {
            m_disabled = Boolean.getBoolean("reasonBsbDisabled");
            if (m_disabled) {
                // Possible but you should NEVER do that
                Notifications.Bus.notify(new ORNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            }
        }

        return m_disabled;
    }
}
