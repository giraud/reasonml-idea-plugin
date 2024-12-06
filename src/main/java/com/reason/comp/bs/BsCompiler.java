package com.reason.comp.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.notification.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.hints.*;
import com.reason.ide.console.*;
import com.reason.ide.console.bs.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.concurrent.atomic.*;

@Service(Service.Level.PROJECT)
public final class BsCompiler implements ORCompiler {
    private static final Log LOG = Log.create("bs.compiler");

    private final Project myProject;

    private final AtomicBoolean myRefreshNinjaIsNeeded = new AtomicBoolean(true);
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    private Boolean myDisabled = null; // Never call directly, use isDisabled()
    private Ninja myNinja = new Ninja(null);

    private BsCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull CompilerType getType() {
        return CompilerType.BS;
    }

    public void refreshNinjaBuild() {
        myRefreshNinjaIsNeeded.compareAndSet(false, true);
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        return new BsProcess(myProject).getFullVersion(file);
    }

    public @NotNull String getNamespace(@NotNull VirtualFile sourceFile) {
        BsConfig bsConfig = myProject.getService(ORCompilerConfigManager.class).getNearestConfig(sourceFile);
        return bsConfig == null ? "" : bsConfig.getNamespace();
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        run(file, CliType.Bs.MAKE, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        if (!isDisabled() && myProject.getService(ORSettings.class).isBsEnabled()) {
            VirtualFile configFile = BsPlatform.findConfigFile(myProject, file);
            BsConfig bsConfig = myProject.getService(ORCompilerConfigManager.class).getConfig(configFile);
            if (configFile == null || bsConfig == null) {
                return;
            }

            if (myProcessStarted.compareAndSet(false, true)) {
                ProcessHandler bscHandler = new BsProcess(myProject).create(configFile, cliType, onProcessTerminated);
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
                    myProject.getService(InsightManager.class).downloadRincewindIfNeeded(configFile);
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
        return !BsPlatform.findConfigFiles(project).isEmpty();
    }

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

    public @NotNull Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        if (myRefreshNinjaIsNeeded.get() && contentRoot != null) {
            VirtualFile ninjaFile = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninjaFile != null) {
                myNinja = new Ninja(FileUtil.readFileContent(ninjaFile));
            }
        }

        return myNinja;
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
    public boolean isAvailable() {
        return !myProcessStarted.get();
    }
}
