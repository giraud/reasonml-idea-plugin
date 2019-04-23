package com.reason.build.bs.compiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.build.CompilerProcessLifecycle;
import com.reason.build.bs.ModuleConfiguration;
import com.reason.build.console.CliType;
import com.reason.ide.ORNotification;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.execution.process.ProcessOutputTypes.STDOUT;
import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public final class BsProcess implements CompilerProcessLifecycle, ProjectComponent {

    @NotNull
    private final Project m_project;

    @Nullable
    private BsProcessHandler m_bsb;
    private RawProcessListener m_outputListener;
    private final AtomicBoolean m_started = new AtomicBoolean(false);
    private final AtomicBoolean m_restartNeeded = new AtomicBoolean(false);

    public static BsProcess getInstance(Project project) {
        return project.getComponent(BsProcess.class);
    }

    public BsProcess(@NotNull Project project) {
        m_project = project;
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        VirtualFile sourceFile = baseRoot.findChild("bsconfig.json");
        if (sourceFile != null) {
            create(sourceFile, CliType.make);
        }
    }

    @Override
    public void projectClosed() {
        killIt();
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
    public ProcessHandler create(@NotNull VirtualFile sourceFile, @NotNull CliType cliType) {
        try {
            return createProcessHandler(sourceFile, cliType);
        } catch (ExecutionException e) {
            // Don't log when first time execution
        }

        return null;
    }

    @Nullable
    public ProcessHandler recreate(@NotNull VirtualFile sourceFile, @NotNull CliType cliType) {
        try {
            return createProcessHandler(sourceFile, cliType);
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new ORNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }

        return null;
    }

    @Nullable
    private ProcessHandler createProcessHandler(@NotNull VirtualFile sourceFile, @NotNull CliType cliType) throws ExecutionException {
        killIt();
        GeneralCommandLine cli = getGeneralCommandLine(sourceFile, cliType);
        if (cli != null) {
            m_bsb = new BsProcessHandler(cli);
            if (m_outputListener == null) {
                addListener(new BsOutputListener(m_project, this));
            } else {
                m_bsb.addRawProcessListener(m_outputListener);
            }
        }
        return m_bsb;
    }

    private void killIt() {
        if (m_bsb != null) {
            m_bsb.killProcess();
            m_bsb = null;
        }
    }

    private void addListener(RawProcessListener outputListener) {
        m_outputListener = outputListener;
        if (m_bsb != null) {
            m_bsb.addRawProcessListener(outputListener);
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(@NotNull VirtualFile sourceFile, @NotNull CliType cliType) {
        String bsbPath = ModuleConfiguration.getBsbPath(m_project, sourceFile);

        if (bsbPath == null) {
            if (!sourceFile.getPath().contains("node_modules")) {
                Notifications.Bus.notify(new ORNotification("Bsb",
                        "<html>Can't find bsb.\n"
                                + "Working directory is '" + ReasonSettings.getInstance(m_project).getWorkingDir(sourceFile) + "'.\n"
                                + "Be sure that bsb is installed and reachable from that directory, "
                                + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#bucklescript\">github</a>.</html>",
                        ERROR, URL_OPENING_LISTENER));
            }
            return null;
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

        cli.withWorkDirectory(ReasonSettings.getInstance(m_project).getWorkingDir(sourceFile));
        cli.withEnvironment("NINJA_ANSI_FORCED", "1");

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
        if (m_bsb != null) {
            m_bsb.notifyTextAvailable("Compilation ended\n", STDOUT);
        }
        m_started.set(false);
    }

    @Nullable
    public String getOCamlVersion() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(ModuleConfiguration.getBscPath(m_project) + " -version");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return ocamlVersionExtractor(reader.readLine());
        } catch (@NotNull InterruptedException | IOException e) {
            return null;
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
    }

    private static final Pattern BS_VERSION_REGEXP = Pattern.compile("BuckleScript (\\d\\.\\d.\\d)[^ ]* \\(Using OCaml(\\d\\.\\d+).+\\)");

    @NotNull
    static String ocamlVersionExtractor(@NotNull String line) {
        Matcher matcher = BS_VERSION_REGEXP.matcher(line);
        if (matcher.matches()) {
            return matcher.group(2);
        }
        return "4.02";
    }
}
