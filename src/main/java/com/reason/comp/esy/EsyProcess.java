package com.reason.comp.esy;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class EsyProcess {
    private static final Log LOG = Log.create("process.esy");

    private final @NotNull Project myProject;

    @Nullable
    private KillableColoredProcessHandler processHandler;

    EsyProcess(@NotNull Project project) {
        myProject = project;
    }

    public void startNotify() {
        if (processHandler != null && !processHandler.isStartNotified()) {
            try {
                processHandler.startNotify();
            } catch (Exception e) {
                LOG.error("Exception when calling 'startNotify'.", e);
            }
        }
    }

    @Nullable
    public ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        killIt();

        Optional<VirtualFile> workingDirOptional = findWorkingDirectory(myProject);
        if (workingDirOptional.isEmpty()) {
            return null;
        }

        Optional<VirtualFile> esyExecutableOptional = BsPlatform.findEsyExecutable(myProject);
        if (esyExecutableOptional.isEmpty()) {
            return null;
        }

        VirtualFile workingDir = workingDirOptional.get();
        VirtualFile esyExecutable = esyExecutableOptional.get();

        GeneralCommandLine cli = newCommandLine(esyExecutable, workingDir, (CliType.Esy) cliType);
        try {
            processHandler = new KillableColoredProcessHandler(cli);
        } catch (ExecutionException e) {
            EsyNotification.showExecutionException(e);
            return null;
        }

        if (onProcessTerminated != null) {
            processHandler.addProcessListener(processTerminatedListener.apply(onProcessTerminated));
        }

        return processHandler;
    }

    private void killIt() {
        if (processHandler == null || processHandler.isProcessTerminating() || processHandler.isProcessTerminated()) {
            return;
        }
        processHandler.killProcess();
        processHandler = null;
    }

    private static GeneralCommandLine newCommandLine(@NotNull VirtualFile esyExecutable, @NotNull VirtualFile workingDir, @NotNull CliType.Esy cliType) {
        GeneralCommandLine commandLine;
        commandLine = new GeneralCommandLine(esyExecutable.getPath());
        commandLine.setWorkDirectory(workingDir.getPath());
        commandLine.setRedirectErrorStream(true);
        commandLine.addParameter(getCommand(cliType)); // 'esy + command' must be a single parameter
        return commandLine;
    }

    private static @NotNull String getCommand(@NotNull CliType.Esy cliType) {
        return switch (cliType) {
            case INSTALL -> "install";
            case BUILD -> "build";
            case SHELL -> "shell";
        };
    }

    private static Optional<VirtualFile> findWorkingDirectory(@NotNull Project project) {
        Optional<VirtualFile> esyContentRootOptional =
                ORProjectManager.findFirstEsyContentRoot(project);
        if (esyContentRootOptional.isEmpty()) {
            EsyNotification.showEsyProjectNotFound();
            return Optional.empty();
        }
        return esyContentRootOptional;
    }

    private static final Function<ORProcessTerminated<Void>, ProcessListener> processTerminatedListener = (onProcessTerminated) ->
            new ProcessAdapter() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    onProcessTerminated.run(null);
                }
            };
}
