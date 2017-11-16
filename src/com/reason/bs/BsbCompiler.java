package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.ide.RmlNotification;
import org.jetbrains.annotations.Nullable;

public class BsbCompiler extends AbstractProjectComponent {

    private KillableColoredProcessHandler m_bsb;
    private @Nullable GeneralCommandLine m_commandLine;
    private ProcessListener m_outputListener;

    protected BsbCompiler(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        String bsbBin = Platform.getBinary("REASON_BSB_BIN", "reasonBsb", "node_modules/bs-platform/bin/bsb.exe");
        if (bsbBin == null) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Bsb is disabled, you need to manually launch an external process", NotificationType.WARNING));
            return;
        }

        VirtualFile baseDir = Platform.findBaseRoot(myProject);
        String bsbPath = Platform.getBinaryPath(myProject, bsbBin);
        if (bsbPath == null) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Can't find bsb using value '" + bsbBin + "' from property 'reasonBsb'.\nBase directory is '" + baseDir.getCanonicalPath() + "'.\nBe sure that bsb is installed and reachable from base directory.", NotificationType.ERROR));
            return;
        }

        m_commandLine = new GeneralCommandLine(bsbPath, "-no-color", "-make-world");
        m_commandLine.setWorkDirectory(baseDir.getCanonicalPath());

        recreate();
    }

    @Override
    public void projectClosed() {
        killIt();
    }

    @Nullable
    ProcessHandler getHandler() {
        return m_bsb;
    }

    // Wait for the tool window to be ready before starting the process
    public void startNotify() {
        if (m_bsb != null) {
            m_bsb.startNotify();
        }
    }

    @Nullable
    public ProcessHandler recreate() {
        if (m_commandLine != null) {
            try {
                killIt();
                m_bsb = new KillableColoredProcessHandler(m_commandLine);
                if (m_outputListener != null) {
                    m_bsb.addProcessListener(m_outputListener);
                }
                return m_bsb;
            } catch (ExecutionException e) {
                Notifications.Bus.notify(new RmlNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
            }
        }

        return null;
    }

    private void killIt() {
        if (m_bsb != null) {
            m_bsb.killProcess();
            m_bsb = null;
        }
    }

    void addListener(ProcessListener outputListener) {
        m_outputListener = outputListener;
        if (m_bsb != null) {
            m_bsb.addProcessListener(outputListener);
        }
    }
}
