package com.reason.comp.dune;

import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.hints.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.concurrent.atomic.*;

public class DuneCompiler implements Compiler {
    private static final Log LOG = Log.create("dune.compiler");

    private final @NotNull Project myProject;
    private final AtomicBoolean myProcessStarted = new AtomicBoolean(false);

    DuneCompiler(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public @NotNull CompilerType getType() {
        return CompilerType.DUNE;
    }

    @Override
    public @NotNull String getFullVersion(@Nullable VirtualFile file) {
        /* Dune version, but we don't care in fact. We are interested in OCaml version. *
        GeneralCommandLine cli = new DuneProcess.DuneCommandLine(myProject, "dune")
                .addParameters(CliType.Dune.VERSION)
                .create(file);

        try (InputStream inputStream = Runtime.getRuntime().exec(cli.getCommandLineString(), new String[]{}, cli.getWorkDirectory()).getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.readLine();
        } catch (IOException e) {
            return "error: " + e.getMessage();
        }
        /*
        String version = "unknown ";

        version += " - not implemented yet";
        //DuneFacet duneFacet = DunePlatform.getFacet(myProject, file);
        //if (duneFacet == null) {
        //    return version + "(dune facet not found)";
        //} else {
        //    Sdk odk = duneFacet.getODK();
        //    SdkTypeId sdkType = odk == null ? null : odk.getSdkType();
        //    if (sdkType == null) {
        //        return version + "(SDK not found)";
        //    }
        //    version = sdkType.getVersionString(odk);
        //}

        return "Dune ( OCaml:" + version + " )";
        */
        ORSettings settings = myProject.getService(ORSettings.class);
        return "Dune (Switch: " + settings.getSwitchName() + ")";
    }

    @Override
    public boolean isConfigured(@NotNull Project project) {
        ORSettings settings = project.getService(ORSettings.class);
        return !settings.getOpamLocation().isEmpty() && settings.getSwitchName() != null;
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
    public void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        run(file, CliType.Dune.BUILD, onProcessTerminated);
    }

    @Override
    public void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
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
            ProcessHandler processHandler = sourceFile == null ? null : process.create(sourceFile, cliType, onProcessTerminated);
            if (processHandler != null && console != null) {
                processHandler.addProcessListener(new CompilerOutputListener(myProject, new DuneOutputAnalyzer()));
                processHandler.addProcessListener(new ProcessFinishedListener());
                processHandler.addProcessListener(new ProcessAdapter() {
                    @Override public void processTerminated(@NotNull ProcessEvent event) {
                        myProcessStarted.compareAndSet(true, false);
                    }
                });

                console.attachToProcess(processHandler);
                process.startNotify();

                myProject.getService(InsightManager.class).downloadRincewindIfNeeded(sourceFile);
            } else {
                myProcessStarted.compareAndSet(true, false);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return myProcessStarted.get();
    }
}
