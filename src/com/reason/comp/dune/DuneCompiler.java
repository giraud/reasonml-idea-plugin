package com.reason.comp.dune;

import com.intellij.execution.process.*;
import com.intellij.facet.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.Compiler;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.facet.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.concurrent.atomic.*;

public class DuneCompiler implements Compiler {
    private static final Log LOG = Log.create("dune.compiler");

    private final @NotNull Project myProject;
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);
    private final AtomicBoolean myConfigurationWarning = new AtomicBoolean(false);

    DuneCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        return "unknown";
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            DuneFacet duneFacet = FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
            if (duneFacet != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        return true; // Not implemented yet
    }

    @Override
    public void refresh(@NotNull VirtualFile configFile) {
        // Nothing to do
    }

    @Override
    public void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated) {
        run(file, CliType.Dune.BUILD, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        if (!(cliType instanceof CliType.Dune)) {
            LOG.error("Invalid cliType for dune compiler. cliType = " + cliType);
            return;
        }
        if (myProject.isDisposed()) {
            return;
        }

        if (myProcessStarted.compareAndSet(false, true)) {
            VirtualFile sourceFile = file == null ? ORProjectManager.findFirstDuneConfigFile(myProject) : file;
            DuneConsoleView console = (DuneConsoleView) myProject.getService(ORToolWindowManager.class).getConsoleView(DuneToolWindowFactory.ID);
            DuneProcess process = new DuneProcess(myProject);
            ProcessHandler processHandler = sourceFile == null ? null : process.create(sourceFile, cliType, myConfigurationWarning, onProcessTerminated);
            if (processHandler != null && console != null) {
                processHandler.addProcessListener(new DuneOutputListener(myProject));
                processHandler.addProcessListener(new ProcessFinishedListener());
                processHandler.addProcessListener(new ProcessAdapter() {
                    @Override public void processTerminated(@NotNull ProcessEvent event) {
                        myProcessStarted.set(false);
                    }
                });

                console.attachToProcess(processHandler);
                process.startNotify();

                myProject.getService(InsightManager.class).downloadRincewindIfNeeded(sourceFile);
            } else {
                myProcessStarted.set(false);
            }
        }
    }
}
