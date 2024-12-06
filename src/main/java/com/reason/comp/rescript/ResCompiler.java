package com.reason.comp.rescript;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.hints.*;
import com.reason.ide.console.*;
import com.reason.ide.console.rescript.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.concurrent.atomic.*;

import static com.reason.comp.CliType.Rescript.*;

@Service(Service.Level.PROJECT)
public final class ResCompiler implements ORCompiler {
    private static final Log LOG = Log.create("compiler.rescript");

    private final Project myProject;
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    private ResCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull CompilerType getType() {
        return CompilerType.RESCRIPT;
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        VirtualFile bscExecutable = ResPlatform.findBscExecutable(myProject, file);
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
    public void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        run(file, MAKE, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile sourceFile, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        LOG.debug("Run compiler");

        ORSettings settings = myProject.getService(ORSettings.class);
        if (settings.isBsEnabled()) {
            if (sourceFile != null) {
                myProject.getService(InsightManager.class).downloadRincewindIfNeeded(sourceFile);
            }

            VirtualFile configFile = ResPlatform.findConfigFile(myProject, sourceFile);
            VirtualFile bin = ResPlatform.findRescriptExecutable(myProject, configFile);

            ConsoleView console = myProject.getService(ORToolWindowManager.class).getConsoleView(RescriptToolWindowFactory.ID);

            if (configFile != null && bin != null && console != null) {
                try {
                    if (myProcessStarted.compareAndSet(false, true)) {
                        GeneralCommandLine cli = getCommandLine(bin.getPath(), (CliType.Rescript) cliType);

                        VirtualFile configParent = configFile.getParent();
                        if (configParent != null) {
                            cli.withWorkDirectory(configParent.getPath());
                        }
                        cli.withEnvironment("NINJA_ANSI_FORCED", "1");
                        if (!settings.isUseSuperErrors()) {
                            cli.withEnvironment("BS_VSCODE", "1");
                        }

                        ResProcessHandler processHandler = new ResProcessHandler(cli);
                        processHandler.addProcessListener(new ProcessFinishedListener(System.currentTimeMillis()));
                        processHandler.addProcessListener(new ProcessAdapter() {
                            @Override
                            public void processTerminated(@NotNull ProcessEvent event) {
                                if (onProcessTerminated != null) {
                                    onProcessTerminated.run(null);
                                }
                                // When build is done, we need to refresh editors to be notified of the latest modifications
                                LOG.debug("Compilation process terminated, restart daemon code analyzer for all edited files");
                                DaemonCodeAnalyzer.getInstance(myProject).restart();

                                myProcessStarted.compareAndSet(true, false);
                            }
                        });

                        console.attachToProcess(processHandler);
                        processHandler.startNotify();
                    }
                } catch (ExecutionException e) {
                    ORNotification.notifyError("Rescript", "Execution exception", e.getMessage());
                    myProcessStarted.compareAndSet(true, false);
                }
            }
        }
    }

    private @NotNull GeneralCommandLine getCommandLine(@NotNull String binPath, @NotNull CliType.Rescript cliType) {
        return switch (cliType) {
            case MAKE -> new GeneralCommandLine(binPath, "build");
            case CLEAN -> new GeneralCommandLine(binPath, "clean");
        };
    }

    public @NotNull Ninja readNinjaBuild(@Nullable VirtualFile contentRoot) {
        @NotNull Ninja ninja = new Ninja(null);

        if (/*m_refreshNinjaIsNeeded.get() &&*/ contentRoot != null) {
            VirtualFile ninjaFile = contentRoot.findFileByRelativePath("lib/bs/build.ninja");
            if (ninjaFile != null) {
                ninja = new Ninja(FileUtil.readFileContent(ninjaFile));
            }
        }

        return ninja;
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        return true; // No project-level configuration
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        VirtualFile bin = ResPlatform.findBscExecutable(project, null);
        return bin != null;
    }

    @Override
    public boolean isAvailable() {
        return !myProcessStarted.get();
    }
}
