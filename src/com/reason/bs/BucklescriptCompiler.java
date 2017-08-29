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

import java.io.File;

public class BucklescriptCompiler extends AbstractProjectComponent {

    private KillableColoredProcessHandler m_bsb;
    private GeneralCommandLine m_commandLine;
    private ProcessListener m_outputListener;

    protected BucklescriptCompiler(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        String reasonBsb = System.getProperty("reasonBsb");
        if (reasonBsb == null) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Bsb is disabled, you need to manually launch an external process", NotificationType.WARNING));
            return;
        }

        VirtualFile baseDir = Platform.findBaseRoot(this.myProject);

        String bsbPath;
        if (new File(reasonBsb).isAbsolute()) {
            bsbPath = reasonBsb;
        }
        else {
            VirtualFile bsbBinary = baseDir.findFileByRelativePath(reasonBsb);
            if (bsbBinary == null) {
                Notifications.Bus.notify(new RmlNotification("Bsb", "Can't find bsb using value '" + reasonBsb + "' from property 'reasonBsb'.\nBase directory is '" + baseDir.getCanonicalPath() + "'.\nBe sure that bsb is installed and reachable from base directory.", NotificationType.ERROR));
                // Add notification system that watch node_modules
                return;
            }
            bsbPath = bsbBinary.getCanonicalPath();
        }
        
        m_commandLine = new GeneralCommandLine(bsbPath, "-make-world", "-w");
        m_commandLine.setWorkDirectory(baseDir.getCanonicalPath());

        recreate();
/*
        ProcessBuilder processBuilder = new ProcessBuilder(bsbBinary.getCanonicalPath(), "-make-world", "-w")
                .directory(new File(baseDir.getCanonicalPath()))
                .redirectErrorStream(true);

        try {
            m_bsb = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bsb.getInputStream()));
            Notifications.Bus.notify(new RmlNotification("Bsb", "Enabled", "Background compilation is using '" + bsbBinary.getCanonicalPath() + "'", NotificationType.INFORMATION, null));

            m_streamListener = new Thread(() -> {
                String line = null;
                BsbError errorMode = null;

                while (true) {
                    try {
                        line = bufferedReader.readLine();

                        if (errorMode != null) {
                            if (line != null && !line.isEmpty()) {
                                if (line.startsWith("File")) {
                                    errorMode.file = line.substring(5);
                                } else if (line.startsWith("Error")) {
                                    errorMode.errorType = line.substring(7);
                                } else if (line.charAt(0) == ' ') {
                                    // still error
                                } else if (errorMode.errorType != null) {
                                    System.out.println("ERROR DETECTED: " + errorMode);
                                    BucklescriptErrorsManager.getInstance(myProject).setError(errorMode);
                                    errorMode = null;
                                }
                            }
                        } else if (line.startsWith("FAILED")) {
                            errorMode = new BsbError();
                        } else if (line.startsWith(">>>> Start compiling")) {
                            // starts
                            System.out.println("START");
                        } else if (line.startsWith(">>>> Finish compiling")) {
                            System.out.println("STOP");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (line != null && !line.isEmpty()) {
                        if (errorMode != null && errorMode.errorType != null) {
                            errorMode.errors.add(line.trim());
                        }
                        System.out.println("» " + line);
                        // redirect to a console (like wallaby) somewhere ?
                    }
                }
            });
            m_streamListener.start();
        } catch (IOException e) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }
*/
    }

    @Override
    public void projectClosed() {
        killIt();
    }

    @Nullable
    ProcessHandler getHandler() {
        return m_bsb;
    }

    // Wait for the toolwindow to be ready before starting the process
    void startNotify() {
        if (m_bsb != null) {
            m_bsb.startNotify();
        }
    }

    @Nullable
    ProcessHandler recreate() {
        try {
            killIt();
            m_bsb = new KillableColoredProcessHandler(m_commandLine, true);
            if (m_outputListener != null) {
                m_bsb.addProcessListener(m_outputListener);
            }
            return m_bsb;
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new RmlNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
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
