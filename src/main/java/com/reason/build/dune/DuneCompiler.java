package com.reason.build.dune;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notifications;
import com.intellij.openapi.projectRoots.Sdk;
import com.reason.build.CompilerLifecycle;
import com.reason.build.bs.ModuleConfiguration;
import com.reason.build.bs.compiler.CliType;
import com.reason.ide.RmlNotification;
import com.reason.ide.sdk.OCamlSDK;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public final class DuneCompiler implements CompilerLifecycle {

    private final ModuleConfiguration m_moduleConfiguration;

    private KillableColoredProcessHandler m_processHangler;
    private ProcessListener m_outputListener;
    private final AtomicBoolean m_started = new AtomicBoolean(false);
    private final AtomicBoolean m_restartNeeded = new AtomicBoolean(false);

    public DuneCompiler(ModuleConfiguration moduleConfiguration) {
        m_moduleConfiguration = moduleConfiguration;
        m_outputListener = new DuneOutputListener(moduleConfiguration.getProject(), this);

        recreate(CliType.make);
    }

    @Nullable
    public ProcessHandler getHandler() {
        return m_processHangler;
    }

    // Wait for the tool window to be ready before starting the process
    public void startNotify() {
        if (m_processHangler != null && !m_processHangler.isStartNotified()) {
            try {
                m_processHangler.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    @Nullable
    public ProcessHandler recreate(CliType cliType) {
        try {
            killIt();
            GeneralCommandLine cli = getGeneralCommandLine(cliType);
            if (cli != null) {
                m_processHangler = new KillableColoredProcessHandler(cli);
                if (m_outputListener != null) {
                    m_processHangler.addProcessListener(m_outputListener);
                }
            }
            return m_processHangler;
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new RmlNotification("Dune", "Can't run sdk\n" + e.getMessage(), ERROR));
        }

        return null;
    }

    public void killIt() {
        if (m_processHangler != null) {
            m_processHangler.killProcess();
            m_processHangler = null;
        }
    }

    public void addListener(ProcessListener outputListener) {
        m_outputListener = outputListener;
        if (m_processHangler != null) {
            m_processHangler.addProcessListener(outputListener);
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(CliType cliType) {
        Sdk sdk = OCamlSDK.getSDK(m_moduleConfiguration.getProject());
        if (sdk == null) {
            Notifications.Bus.notify(new RmlNotification("Dune",
                    "<html>Can't find sdk.\n"
                            + "Working directory is '" + m_moduleConfiguration.getWorkingDir() + "'.\n"
                            + "Be sure that sdk is installed and reachable from that directory, "
                            + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#dune\">github</a>.</html>",
                    ERROR, URL_OPENING_LISTENER));
            return null;
        }

        GeneralCommandLine cli = new GeneralCommandLine(sdk.getHomePath() + "/bin/jbuilder", "build", "rincewind.exe");
        cli.withWorkDirectory(m_moduleConfiguration.getWorkingDir());

        return cli;
    }

    public boolean start() {
        boolean success = m_started.compareAndSet(false, true);
        if (!success) {
            m_restartNeeded.compareAndSet(false, true);
        }
        return success;
    }

    public void terminated() {
        m_started.set(false);
    }

}
