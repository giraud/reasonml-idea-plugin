package com.reason.comp.rescript;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.console.rescript.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.concurrent.atomic.*;

import static com.reason.comp.ORConstants.*;
import static com.reason.comp.CliType.Rescript.*;

public class ResCompiler implements Compiler {
    private static final Log LOG = Log.create("compiler.rescript");
    private final @NotNull Project myProject;
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    private ResCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        VirtualFile bsConfig = file == null ? ResPlatform.findConfigFile(myProject) : ORFileUtils.findAncestor(myProject, BS_CONFIG_FILENAME, file);
        VirtualFile bscExecutable = bsConfig == null ? null : ResPlatform.findBscExecutable(myProject, bsConfig);

        if (bscExecutable != null) {
            try (InputStream inputStream = Runtime.getRuntime().exec(bscExecutable.getPath() + " -version").getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.readLine();
            } catch (IOException e) {
                return "error: " + e.getMessage();
            }
        }

        return "unknown (bsc not found)";
    }

    @Override
    public void refresh(@NotNull VirtualFile bsConfigFile) {
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated) {
        run(file, MAKE, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile sourceFile, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated) {
        if (!isDisabled() && myProject.getService(ORSettings.class).isBsEnabled()) {
            // ResPlatform.findRescriptExe
            VirtualFile bsConfig = sourceFile == null ? ResPlatform.findConfigFile(myProject) : ORFileUtils.findAncestor(myProject, BS_CONFIG_FILENAME, sourceFile);
            if (bsConfig == null) {
                return;
            }

            VirtualFile binDir = ResPlatform.findBinaryPathForConfigFile(myProject, bsConfig);
            VirtualFile bin = binDir == null ? null : ORPlatform.findBinary(binDir, RESCRIPT_EXE_NAME);

            ConsoleView console = myProject.getService(ORToolWindowManager.class).getConsoleView(RescriptToolWindowFactory.ID);

            if (bin != null && console != null) {
                try {
                    GeneralCommandLine cli = getCommandLine(bin.getPath(), (CliType.Rescript) cliType);

                    cli.withWorkDirectory(bsConfig.getParent().getPath());
                    cli.withEnvironment("NINJA_ANSI_FORCED", "1");

                    ResProcessHandler processHandler = new ResProcessHandler(cli);  // m_processHandler = new KillableColoredProcessHandler(cli);
                    if (myProcessStarted.compareAndSet(false, true)) {
                        processHandler.addProcessListener(new ProcessFinishedListener(System.currentTimeMillis()));
                        processHandler.addProcessListener(new ProcessAdapter() {
                            @Override public void processTerminated(@NotNull ProcessEvent event) {
                                myProcessStarted.set(false);
                                // When build is done, we need to refresh editors to be notified of the latest modifications
                                LOG.debug("Compilation process terminated, restart daemon code analyzer for all edited files");
                                DaemonCodeAnalyzer.getInstance(myProject).restart();
                            }
                        });

                        console.attachToProcess(processHandler);
                        processHandler.startNotify();

                        if (sourceFile != null) {
                            myProject.getService(InsightManager.class).downloadRincewindIfNeeded(sourceFile);
                        }
                    }
                } catch (ExecutionException e) {
                    ORNotification.notifyError("Rescript", "Execution exception", e.getMessage(), null);
                    //return null;
                }
            }
        }
    }

    private GeneralCommandLine getCommandLine(@NotNull String binPath, @NotNull CliType.Rescript cliType) {
        switch (cliType) {
            case MAKE:
                return new GeneralCommandLine(binPath, "build");
            case CLEAN:
                return new GeneralCommandLine(binPath, "clean");
            default:
                return new GeneralCommandLine(binPath);
        }
    }

    public @NotNull Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        @NotNull Ninja m_ninja = new Ninja(null);

        if (/*m_refreshNinjaIsNeeded.get() &&*/ contentRoot != null) {
            VirtualFile ninjaFile = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninjaFile != null) {
                m_ninja = new Ninja(FileUtil.readFileContent(ninjaFile));
            }
        }

        return m_ninja;
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        return true; // No project-level configuration
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        VirtualFile bsConfig = ORProjectManager.findFirstBsConfigurationFile(project).orElse(null);
        VirtualFile rescriptBin = bsConfig == null ? null : ResPlatform.findBinaryPathForConfigFile(project, bsConfig);

        return rescriptBin != null;
    }

    private boolean isDisabled() {
        return false;
    }
}
