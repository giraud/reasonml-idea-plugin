package com.reason.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.notification.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.reason.*;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;
import org.jetbrains.coverage.gnu.trove.*;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class BsCompilerImpl implements BsCompiler {
    private static final Log LOG = Log.create("compiler.bs");

    private final @NotNull Project m_project;
    private final Map<String, BsConfig> m_configs = new THashMap<>();
    private final AtomicBoolean m_refreshNinjaIsNeeded = new AtomicBoolean(true);

    private @Nullable Boolean m_disabled = null; // Never call directly, use isDisabled()
    private @NotNull Ninja m_ninja = new Ninja(null);

    private BsCompilerImpl(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public void refreshNinjaBuild() {
        m_refreshNinjaIsNeeded.compareAndSet(false, true);
    }

    @Override
    public @NotNull String getNamespace(@NotNull VirtualFile sourceFile) {
        Optional<VirtualFile> bsConfigFile = BsPlatform.findBsConfigForFile(m_project, sourceFile);
        if (bsConfigFile.isPresent()) {
            BsConfig bsConfig = getOrRefreshBsConfig(bsConfigFile.get());
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
        if (!isDisabled() && ORSettings.getInstance(m_project).isBsEnabled()) {
            Optional<VirtualFile> bsConfigFile = BsPlatform.findBsConfigForFile(m_project, sourceFile);
            if (!bsConfigFile.isPresent()) {
                return;
            }
            getOrRefreshBsConfig(bsConfigFile.get());
            BsProcess process = ServiceManager.getService(m_project, BsProcess.class);
            if (process.start()) {
                ProcessHandler bscHandler = process.create(sourceFile, cliType, onProcessTerminated);
                if (bscHandler != null) {
                    ConsoleView console = getConsoleView();
                    if (console != null) {
                        long start = System.currentTimeMillis();
                        console.attachToProcess(bscHandler);
                        bscHandler.addProcessListener(new ProcessFinishedListener(start));
                        bscHandler.addProcessListener(new ProcessListener() {
                            @Override
                            public void startNotified(@NotNull ProcessEvent event) {
                            }

                            @Override
                            public void processTerminated(@NotNull ProcessEvent event) {
                                LOG.debug("Compilation process terminated, restart daemon code analyzer for all edited files");
                                DaemonCodeAnalyzer.getInstance(m_project).restart();
                            }

                            @Override
                            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                            }
                        });
                    }
                    process.startNotify();
                    ServiceManager.getService(m_project, InsightManager.class)
                            .downloadRincewindIfNeeded(sourceFile);
                } else {
                    process.terminate();
                }
            }
        }
    }

    @Override
    public @NotNull CompilerType getType() {
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
        Optional<VirtualFile> bsConfigFile = BsPlatform.findBsConfigForFile(m_project, file);
        BsConfig bsConfig = bsConfigFile.map(this::getOrRefreshBsConfig).orElse(null);
        return bsConfig == null || bsConfig.accept(file.getPath());
    }

    @Override
    public @Nullable String convert(@NotNull VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document) {
        RefmtProcess refmt = RefmtProcess.getInstance(m_project);
        String oldText = document.getText();
        String newText = refmt.convert(virtualFile, isInterface, fromFormat, toFormat, oldText);
        // additional protection
        return oldText.isEmpty() || newText.isEmpty() ? null : newText;
    }

    @Override
    public @NotNull Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        if (m_refreshNinjaIsNeeded.get() && contentRoot != null) {
            VirtualFile ninjaFile = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninjaFile != null) {
                m_ninja = new Ninja(FileUtil.readFileContent(ninjaFile));
            }
        }

        return m_ninja;
    }

    @Override
    public @Nullable ConsoleView getConsoleView() {
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
    // endregion

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
