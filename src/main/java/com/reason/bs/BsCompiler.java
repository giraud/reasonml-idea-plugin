package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.RmlNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BsCompiler {

    private final String m_canonicalPath;
    private final String m_bsbPath;

    private KillableColoredProcessHandler m_bsb;
    private ProcessListener m_outputListener;

    BsCompiler(VirtualFile baseDir, String bsbPath) {
        m_canonicalPath = baseDir.getCanonicalPath();
        m_bsbPath = bsbPath;
        recreate(CliType.make);
    }

    @Nullable
    public ProcessHandler getHandler() {
        return m_bsb;
    }

    // Wait for the tool window to be ready before starting the process
    public void startNotify() {
        if (m_bsb != null && !m_bsb.isStartNotified()) {
            try {
                m_bsb.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    @Nullable
    public ProcessHandler recreate(CliType cliType) {
        try {
            killIt();
            m_bsb = new KillableColoredProcessHandler(getGeneralCommandLine(cliType));
            if (m_outputListener != null) {
                m_bsb.addProcessListener(m_outputListener);
            }
            return m_bsb;
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }

        return null;
    }

    void killIt() {
        if (m_bsb != null) {
            m_bsb.killProcess();
            m_bsb = null;
        }
    }

    public void addListener(ProcessListener outputListener) {
        m_outputListener = outputListener;
        if (m_bsb != null) {
            m_bsb.addProcessListener(outputListener);
        }
    }

    @NotNull
    private GeneralCommandLine getGeneralCommandLine(CliType cliType) {
        GeneralCommandLine cli;
        switch (cliType) {
            case make:
                cli = new GeneralCommandLine(m_bsbPath, "-make-world");
                break;
            case cleanMake:
                cli = new GeneralCommandLine(m_bsbPath, "-clean-world", "-make-world");
                break;
            default:
                cli = new GeneralCommandLine(m_bsbPath);

        }
        cli.withWorkDirectory(m_canonicalPath);
        return cli;
    }
}
