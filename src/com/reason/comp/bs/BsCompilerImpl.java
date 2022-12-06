package com.reason.comp.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.notification.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.rescript.*;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.console.bs.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;
import org.jetbrains.coverage.gnu.trove.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static com.reason.comp.ORConstants.*;

public class BsCompilerImpl implements BsCompiler {
    private static final Log LOG = Log.create("compiler.bs");

    private final Project myProject;
    private final Map<String, BsConfig> myConfigs = new THashMap<>();
    private final AtomicBoolean myRefreshNinjaIsNeeded = new AtomicBoolean(true);
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    private Boolean myDisabled = null; // Never call directly, use isDisabled()
    private Ninja myNinja = new Ninja(null);

    private BsCompilerImpl(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull CompilerType getType() {
        return CompilerType.BS;
    }

    @Override
    public void refreshNinjaBuild() {
        myRefreshNinjaIsNeeded.compareAndSet(false, true);
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        VirtualFile root = file == null ? ORProjectManager.findFirstBsContentRoot(myProject) : ORFileUtils.findAncestor(myProject, BS_CONFIG_FILENAME, file);
        if (root != null) {
            return new BsProcess(myProject).getFullVersion(root);
        }
        return "unknown (content root not found)";
    }

    @Override
    public @NotNull String getNamespace(@NotNull VirtualFile sourceFile) {
        VirtualFile bsConfigFile = BsPlatform.findBsConfig(myProject, sourceFile);
        if (bsConfigFile != null) {
            BsConfig bsConfig = getOrRefreshBsConfig(bsConfigFile);
            return bsConfig == null ? "" : bsConfig.getNamespace();
        }
        return "";
    }

    @Override
    public void refresh(@NotNull VirtualFile bsConfigFile) {
        VirtualFile file = bsConfigFile.isDirectory() ? bsConfigFile.findChild(ORConstants.BS_CONFIG_FILENAME) : bsConfigFile;
        if (file != null) {
            BsConfig updatedConfig = BsConfigReader.read(file);
            myConfigs.put(file.getCanonicalPath(), updatedConfig);
        }
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        run(file, CliType.Bs.MAKE, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        if (!isDisabled() && myProject.getService(ORSettings.class).isBsEnabled()) {
            VirtualFile sourceFile = file == null ? ORProjectManager.findFirstBsContentRoot(myProject) : file;
            VirtualFile bsConfigFile = sourceFile == null ? null : BsPlatform.findBsConfig(myProject, sourceFile);
            if (bsConfigFile == null) {
                return;
            }
            getOrRefreshBsConfig(bsConfigFile);

            if (myProcessStarted.compareAndSet(false, true)) {
                ProcessHandler bscHandler = new BsProcess(myProject).create(sourceFile, cliType, onProcessTerminated);
                ConsoleView console = myProject.getService(ORToolWindowManager.class).getConsoleView(BsToolWindowFactory.ID);
                if (bscHandler != null && console != null) {
                    long start = System.currentTimeMillis();
                    bscHandler.addProcessListener(new ProcessFinishedListener(start));
                    bscHandler.addProcessListener(new ProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            myProcessStarted.compareAndSet(true, false);
                            LOG.debug("Compilation process terminated, restart daemon code analyzer for all edited files");
                            DaemonCodeAnalyzer.getInstance(myProject).restart();
                        }
                    });

                    console.attachToProcess(bscHandler);
                    bscHandler.startNotify();
                    myProject.getService(InsightManager.class).downloadRincewindIfNeeded(sourceFile);
                }
            } else {
                myProcessStarted.compareAndSet(true, false);
            }
        }
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        // BuckleScript doesn't require any project-level configuration
        return true;
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        VirtualFile bsConfig = ORProjectManager.findFirstBsConfigurationFile(project).orElse(null);
        VirtualFile bsbBin = bsConfig == null ? null : BsPlatform.findBinaryPathForConfigFile(project, bsConfig);
        VirtualFile resBin = bsConfig == null ? null : ResPlatform.findBinaryPathForConfigFile(project, bsConfig);

        return bsbBin != null && resBin == null;
    }

    @Override
    public boolean isDependency(@Nullable VirtualFile file) {
        if (file == null) {
            return false;
        }
        VirtualFile bsConfigFile = BsPlatform.findBsConfig(myProject, file);
        BsConfig bsConfig = bsConfigFile == null ? null : getOrRefreshBsConfig(bsConfigFile);
        return bsConfig == null || bsConfig.accept(file.getPath());
    }

    @Override
    public @Nullable String convert(@Nullable VirtualFile virtualFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull Document document) {
        String result = null;
        if (virtualFile != null) {
            BsFormatProcess refmt = myProject.getService(BsFormatProcess.class);
            String oldText = document.getText();
            String newText = refmt.convert(virtualFile, isInterface, fromFormat, toFormat, oldText);
            // additional protection
            result = oldText.isEmpty() || newText.isEmpty() ? null : newText;
        }
        return result;
    }

    @Override
    public @NotNull Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        if (myRefreshNinjaIsNeeded.get() && contentRoot != null) {
            VirtualFile ninjaFile = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninjaFile != null) {
                myNinja = new Ninja(FileUtil.readFileContent(ninjaFile));
            }
        }

        return myNinja;
    }

    @Nullable
    private BsConfig getOrRefreshBsConfig(@NotNull VirtualFile bsConfigFile) {
        String bsConfigPath = bsConfigFile.getCanonicalPath();
        BsConfig bsConfig = myConfigs.get(bsConfigPath);
        if (bsConfig == null) {
            refresh(bsConfigFile);
            bsConfig = myConfigs.get(bsConfigPath);
        }
        return bsConfig;
    }
    // endregion

    private boolean isDisabled() {
        if (myDisabled == null) {
            myDisabled = Boolean.getBoolean("reasonBsbDisabled");
            if (myDisabled) {
                // Possible but you should NEVER do that
                Notifications.Bus.notify(new ORNotification("Bsb", "Bucklescript is disabled", NotificationType.WARNING));
            }
        }

        return myDisabled;
    }

    @Override
    public boolean isRunning() {
        return myProcessStarted.get();
    }
}
