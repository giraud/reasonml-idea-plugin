package com.reason.dune;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.concurrent.atomic.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.CompilerProcess;
import com.reason.ORNotification;
import com.reason.bs.BsPlatform;
import com.reason.ide.ORProjectManager;
import com.reason.ide.console.CliType;
import com.reason.sdk.OCamlSdkType;

import static com.intellij.notification.NotificationType.ERROR;

public final class DuneProcess implements CompilerProcess {

    @NotNull
    private final Project m_project;
    @NotNull
    private final ProcessListener m_outputListener;
    @Nullable
    private KillableColoredProcessHandler m_processHandler;
    private final AtomicBoolean m_started = new AtomicBoolean(false);

    DuneProcess(@NotNull Project project) {
        m_project = project;
        m_outputListener = new DuneOutputListener(m_project, this);
    }

    public static DuneProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, DuneProcess.class);
    }

    // Wait for the tool window to be ready before starting the process
    @Override
    public void startNotify() {
        if (m_processHandler != null && !m_processHandler.isStartNotified()) {
            try {
                m_processHandler.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    @Override
    @Nullable
    public ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            killIt();
            GeneralCommandLine cli = getGeneralCommandLine((CliType.Dune) cliType);
            if (cli != null) {
                m_processHandler = new KillableColoredProcessHandler(cli);
                m_processHandler.addProcessListener(m_outputListener);
                if (onProcessTerminated != null) {
                    m_processHandler.addProcessListener(new ProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            onProcessTerminated.run();
                        }
                    });
                }
            }
            return m_processHandler;
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new ORNotification("Dune", "Can't run sdk\n" + e.getMessage(), ERROR));
            return null;
        }
    }

    private void killIt() {
        if (m_processHandler != null) {
            m_processHandler.killProcess();
            m_processHandler = null;
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(CliType.Dune cliType) {
        Sdk odk = OCamlSdkType.getSDK(m_project);
        if (odk == null) {
            DuneNotification.showOcamlSdkNotFound();
            return null;
        }
        assert odk.getHomePath() != null;

        Optional<VirtualFile> baseRoot = ORProjectManager.findFirstDuneContentRoot(m_project);
        if (!baseRoot.isPresent()) {
            return null;
        }

        String duneBinary = BsPlatform.findDuneExecutable(m_project).map(VirtualFile::getPath).orElse("");
        GeneralCommandLine cli;
        switch (cliType) {
            case CLEAN:
                cli = new GeneralCommandLine(duneBinary, "clean");
                break;
            case BUILD:
            default:
                cli = new GeneralCommandLine(duneBinary, "build");
        }

        FileSystem fileSystem = FileSystems.getDefault();
        String ocamlPath = fileSystem.getPath(odk.getHomePath(), "share") + File.pathSeparator + //
                fileSystem.getPath(odk.getHomePath(), "sbin") + File.pathSeparator + //
                fileSystem.getPath(odk.getHomePath(), "lib") + File.pathSeparator + //
                fileSystem.getPath(odk.getHomePath(), "bin") + File.pathSeparator;

        String libPath = fileSystem.getPath(odk.getHomePath(), "lib", "stublibs") + File.pathSeparator + //
                fileSystem.getPath(odk.getHomePath(), "lib", "ocaml") + File.pathSeparator +  //
                fileSystem.getPath(odk.getHomePath(), "lib", "ocaml", "stublibs") + File.pathSeparator;

        Map<String, String> environment = cli.getParentEnvironment();

        cli.withEnvironment("PATH", ocamlPath + File.pathSeparator + environment.get("PATH"));
        cli.withEnvironment("OCAMLLIB", fileSystem.getPath(odk.getHomePath(), "lib", "ocaml").toString());
        cli.withEnvironment("CAML_LD_LIBRARY_PATH", libPath);
        cli.setWorkDirectory(baseRoot.get().getPath());
        cli.setRedirectErrorStream(true);

        return cli;
    }

    @Override
    public boolean start() {
        return m_started.compareAndSet(false, true);
    }

    @Override
    public void terminate() {
        m_started.set(false);
    }
}
