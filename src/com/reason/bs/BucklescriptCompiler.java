package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.ReasonMLNotification;

import java.util.Arrays;

public class BucklescriptCompiler extends AbstractProjectComponent {

    private KillableColoredProcessHandler bsb;

    protected BucklescriptCompiler(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        if (!"true".equals(System.getProperty("reasonBsb"))) {
            Notifications.Bus.notify(new ReasonMLNotification("Bsb", "Bsb is disabled, you need to manually launch an external process", NotificationType.WARNING));
            return;
        }

        VirtualFile baseDir = this.myProject.getBaseDir();
        if (baseDir.findChild("node_modules") == null) {
            // try to find it one level deeper
            baseDir = Arrays.stream(baseDir.getChildren()).filter(file -> file.findChild("node_modules") != null).findFirst().orElse(null);
        }

        VirtualFile bsbBinary = baseDir.findFileByRelativePath("node_modules/bs-platform/bin/bsb.exe");
        if (bsbBinary == null) {
            Notifications.Bus.notify(new ReasonMLNotification("Bsb", "Can't find bsb, be sure that it is installed.", NotificationType.ERROR));
            // Add notification system that watch node_modules
            return;
        }

        GeneralCommandLine commandLine = new GeneralCommandLine(bsbBinary.getCanonicalPath(), "-make-world", "-w");
        commandLine.setWorkDirectory(baseDir.getCanonicalPath());

        try {
            this.bsb = new KillableColoredProcessHandler(commandLine);
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }

/*
        ProcessBuilder processBuilder = new ProcessBuilder(bsbBinary.getCanonicalPath(), "-make-world", "-w")
                .directory(new File(baseDir.getCanonicalPath()))
                .redirectErrorStream(true);

        try {
            this.bsb = processBuilder.start();
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bsb.getInputStream()));
            Notifications.Bus.notify(new ReasonMLNotification("Bsb", "Enabled", "Background compilation is using '" + bsbBinary.getCanonicalPath() + "'", NotificationType.INFORMATION, null));

            this.streamListener = new Thread(() -> {
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
            this.streamListener.start();
        } catch (IOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }
*/
    }

    @Override
    public void projectClosed() {
        if (this.bsb != null) {
            this.bsb.killProcess();
            this.bsb = null;
        }
    }

    // Wait for the toolwindow to be ready before starting the process
    void startNotify() {
        this.bsb.startNotify();
    }

    ProcessHandler getHandler() {
        return this.bsb;
    }
}
