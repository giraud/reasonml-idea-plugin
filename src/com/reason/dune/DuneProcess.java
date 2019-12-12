package com.reason.dune;

import java.io.*;
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.CompilerProcessLifecycle;
import com.reason.OCamlSdkType;
import com.reason.Platform;
import com.reason.ide.ORNotification;
import com.reason.ide.console.CliType;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public final class DuneProcess implements CompilerProcessLifecycle {

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

    // Wait for the tool window to be ready before starting the process
    void startNotify() {
        if (m_processHandler != null && !m_processHandler.isStartNotified()) {
            try {
                m_processHandler.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    @Nullable
    ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            killIt();
            GeneralCommandLine cli = getGeneralCommandLine(cliType);
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
        }

        return null;
    }

    private void killIt() {
        if (m_processHandler != null) {
            m_processHandler.killProcess();
            m_processHandler = null;
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(CliType cliType) {
        Sdk odk = OCamlSdkType.getSDK(m_project);
        if (odk == null) {
            Notifications.Bus.notify(new ORNotification("Dune", "<html>Can't find sdk.\n"
                    + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
                    + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#ocaml\">github</a>.</html>", ERROR, URL_OPENING_LISTENER));
            return null;
        }

        VirtualFile baseRoot = Platform.findORContentRoot(m_project);
        if (baseRoot == null) {
            return null;
        }

        String workingDir = baseRoot.getPath();

        GeneralCommandLine cli;
        switch (cliType) {
            case clean:
                cli = new GeneralCommandLine(odk.getHomePath() + "/bin/dune", "clean");
                break;
            default:
                cli = new GeneralCommandLine(odk.getHomePath() + "/bin/dune", "build");
        }

        Map<String, String> environment = cli.getParentEnvironment();
        String path = environment.get("PATH");
        String newPath = odk.getHomePath() + "/bin" + File.pathSeparator + path;
        cli.withEnvironment("PATH", newPath);
        cli.setWorkDirectory(workingDir);
        cli.setRedirectErrorStream(true);

        return cli;
    }

    public boolean start() {
        return m_started.compareAndSet(false, true);
    }

    @Override
    public void terminated() {
        m_started.set(false);
    }
}
