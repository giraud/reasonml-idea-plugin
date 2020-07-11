package com.reason.esy;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.CompilerProcess;
import com.reason.Log;
import com.reason.Platform;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;

import static com.reason.esy.EsyConstants.ESY_EXECUTABLE_NAME;

public class EsyProcess implements CompilerProcess {

    private static final Log LOG = Log.create("process.esy");

    public static class Command {
        public static final String ESY = "";
        public static final String INSTALL = "install";
        public static final String BUILD = "build";
        public static final String SHELL = "shell";
    }

    private final Project project;

    private final AtomicBoolean isStarted;

    @Nullable
    private KillableColoredProcessHandler processHandler;

    public static EsyProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, EsyProcess.class);
    }

    private EsyProcess(@NotNull Project project) {
        this.project = project;
        this.isStarted = new AtomicBoolean(false);
    }

    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public boolean start() {
        if (isStarted()) {
            LOG.warn("Esy process already started.");
        }
        return isStarted.compareAndSet(false, true);
    }

    @Override
    public void terminate() {
        if (!isStarted()) {
            LOG.warn("Esy process already terminated.");
            return;
        }
        isStarted.set(false);
    }

    @Override
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
    @Override
    public ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        killIt();

        Optional<Path> workingDirOptional = findWorkingDirectory(project);
        if (!workingDirOptional.isPresent()) {
            return null;
        }

        Optional<Path> esyExecutableOptional = findEsyExecutableInPath();
        if (!esyExecutableOptional.isPresent()) {
            return null;
        }

        Path workingDir = workingDirOptional.get();
        Path esyExecutable = esyExecutableOptional.get();

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

    private static GeneralCommandLine newCommandLine(Path esyExecutable, Path workingDir, CliType.Esy cliType) {
        GeneralCommandLine commandLine;
        commandLine = new GeneralCommandLine(esyExecutable.toString());
        commandLine.setWorkDirectory(workingDir.toFile());
        commandLine.setRedirectErrorStream(true);
        commandLine.addParameter(getCommand(cliType)); // 'esy + command' must be a single parameter
        return commandLine;
    }

    private static String getCommand(CliType.Esy cliType) {
        switch (cliType) {
            case INSTALL:
                return Command.INSTALL;
            case BUILD:
                return Command.BUILD;
            case SHELL:
                return Command.SHELL;
            default:
                return Command.ESY;
        }
    }

    private static Optional<Path> findEsyExecutableInPath() {
        String systemPath = System.getenv("PATH");
        Optional<Path> esyExecutablePath = Platform.findExecutableInPath(ESY_EXECUTABLE_NAME, systemPath);
        if (!esyExecutablePath.isPresent()) {
            EsyNotification.showEsyNotFound();
        }
        return esyExecutablePath;
    }

    private static Optional<Path> findWorkingDirectory(@NotNull Project project) {
        Optional<VirtualFile> esyContentRootOptional = ORProjectManager.findFirstEsyContentRoot(project);
        if (!esyContentRootOptional.isPresent()) {
            EsyNotification.showEsyProjectNotFound();
            return Optional.empty();
        }
        VirtualFile esyContentRoot = esyContentRootOptional.get();
        FileSystem fileSystem = FileSystems.getDefault();
        return Optional.of(fileSystem.getPath(esyContentRoot.getPath()));
    }

    private static final Function<Compiler.ProcessTerminated, ProcessListener> processTerminatedListener = (onProcessTerminated) -> new ProcessAdapter() {
        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
            onProcessTerminated.run();
        }
    };
}