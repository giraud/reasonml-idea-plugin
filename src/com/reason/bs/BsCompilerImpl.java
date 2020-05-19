package com.reason.bs;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.coverage.gnu.trove.THashMap;
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
import com.intellij.ui.content.Content;
import com.reason.CompilerType;
import com.reason.FileUtil;
import com.reason.ORNotification;
import com.reason.Platform;
import com.reason.ProcessFinishedListener;
import com.reason.hints.InsightManager;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;
import com.reason.ide.console.ORToolWindowProvider;
import com.reason.ide.settings.ReasonSettings;

public class BsCompilerImpl implements BsCompiler {

    @NotNull
    private final Project m_project;
    private final Map<String, BsConfig> m_configs = new THashMap<>();

    @Nullable
    private Boolean m_disabled = null; // Never call directly, use isDisabled()

    private BsCompilerImpl(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    @Override
    public String getNamespace(@NotNull VirtualFile sourceFile) {
        VirtualFile bsConfigFile = Platform.findAncestorBsconfig(m_project, sourceFile);
        if (bsConfigFile != null) {
            BsConfig bsConfig = getOrRefreshBsConfig(bsConfigFile);
            return bsConfig == null ? "" : bsConfig.getNamespace();
        }
        return "";
    }

    @Override
    @Deprecated
    public Optional<VirtualFile> findFirstContentRoot(@NotNull Project project) {
        return ORProjectManager.findFirstBsContentRoot(project);
    }

    @Override
    public Set<VirtualFile> findContentRoots(@NotNull Project project) {
        return ORProjectManager.findBsContentRoots(project);
    }

    @Override
    public void refresh(@NotNull VirtualFile bsConfigFile) {
        VirtualFile file = bsConfigFile.isDirectory() ? bsConfigFile.findChild(BsConstants.BS_CONFIG_FILENAME) : bsConfigFile;
        if (file != null) {
            BsConfig updatedConfig = BsConfigReader.read(file);
            m_configs.put(file.getCanonicalPath(), updatedConfig);
        }
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated) {
        run(file, CliType.Bs.MAKE, onProcessTerminated);
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
                        ConsoleView console = getConsoleView();
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

    @Override
    public boolean isConfigured(@NotNull Project project) {
        // BuckleScript doesn't require any project-level configuration
        return true;
    }

    @Override
    public boolean isDependency(@Nullable VirtualFile file) {
        if (file == null) {
            return false;
        }

        VirtualFile bsConfigFile = Platform.findAncestorBsconfig(m_project, file);
        BsConfig bsConfig = bsConfigFile == null ? null : getOrRefreshBsConfig(bsConfigFile);
        return bsConfig == null || bsConfig.accept(file.getPath());
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

    @Nullable
    @Override
    public ConsoleView getConsoleView() {
        ORToolWindowProvider windowProvider = ORToolWindowProvider.getInstance(m_project);
        ToolWindow bsToolWindow = windowProvider.getBsToolWindow();
        Content windowContent = bsToolWindow == null ? null : bsToolWindow.getContentManager().getContent(0);
        if (windowContent == null) {
            return null;
        }

        SimpleToolWindowPanel component = (SimpleToolWindowPanel) windowContent.getComponent();
        JComponent panelComponent = component.getComponent();
        return panelComponent == null ? null : (ConsoleView) panelComponent.getComponent(0);
    }

    @Nullable
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
