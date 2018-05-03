package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.RmlNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public final class BsCompiler {

    private final String m_canonicalPath;
    private final Module m_module;

    private KillableColoredProcessHandler m_bsb;
    private ProcessListener m_outputListener;

    BsCompiler(VirtualFile baseDir, Project project) {
        m_canonicalPath = baseDir.getCanonicalPath();
        m_module = ModuleUtil.findModuleForFile(baseDir, project);
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
        String bsbPath = ModuleConfiguration.getBsbPath(m_module);

        if (bsbPath == null) {
            Notifications.Bus.notify(new RmlNotification("Bsb",
                    "<html>Can't find bsb.\n"
                            + "Base directory is '" + m_module.getProject().getBaseDir().getCanonicalPath() + "'.\n"
                            + "Be sure that bsb is installed and reachable from base directory, "
                            + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#bucklescript\">github</a>.</html>",
                    ERROR, URL_OPENING_LISTENER));
        }

        GeneralCommandLine cli;
        switch (cliType) {
            case make:
                cli = new GeneralCommandLine(bsbPath, "-make-world");
                break;
            case cleanMake:
                cli = new GeneralCommandLine(bsbPath, "-clean-world", "-make-world");
                break;
            default:
                cli = new GeneralCommandLine(bsbPath);

        }
        cli.withWorkDirectory(m_canonicalPath);
        return cli;
    }
}
