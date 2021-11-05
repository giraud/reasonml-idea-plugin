package com.reason.comp.esy;

import com.intellij.execution.process.*;
import com.intellij.execution.ui.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.console.esy.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.concurrent.atomic.*;

public class EsyCompiler implements Compiler {
    private static final Log LOG = Log.create("compiler.esy");

    private final @NotNull Project myProject;
    private final @NotNull AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    EsyCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull CompilerType getType() {
        return CompilerType.ESY;
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        return "unknown";
    }

    @Override
    public void refresh(@NotNull VirtualFile configFile) {
        // Nothing to do
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        run(file, CliType.Esy.BUILD, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated) {
        if (!(cliType instanceof CliType.Esy)) {
            LOG.error("Invalid cliType for esy compiler. cliType = " + cliType);
            return;
        }
        if (myProject.isDisposed()) {
            return;
        }

        if (myProcessStarted.compareAndSet(false, true)) {
            VirtualFile sourceFile = file == null ? ORProjectManager.findFirstBsContentRoot(myProject) : file;
            ConsoleView console = myProject.getService(ORToolWindowManager.class).getConsoleView(EsyToolWindowFactory.ID);

            if (sourceFile != null && console != null) {
                EsyProcess process = new EsyProcess(myProject);
                ProcessHandler processHandler = process.create(sourceFile, cliType, onProcessTerminated);
                if (processHandler != null) {
                    processHandler.addProcessListener(new ProcessFinishedListener());
                    processHandler.addProcessListener(new ProcessAdapter() {
                        @Override public void processTerminated(@NotNull ProcessEvent event) {
                            myProcessStarted.compareAndSet(true, false);
                        }
                    });

                    console.attachToProcess(processHandler);
                    process.startNotify();
                } else {
                    myProcessStarted.compareAndSet(true, false);
                }
            }
        }
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        // Esy compiler doesn't require any project-level configuration
        return true;
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true; // not implemented yet
    }

    @Override
    public boolean isRunning() {
        return myProcessStarted.get();
    }
}
