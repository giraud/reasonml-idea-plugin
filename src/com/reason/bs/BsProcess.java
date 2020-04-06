package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.CompilerProcess;
import com.reason.Platform;
import com.reason.ide.ORNotification;
import com.reason.ide.console.CliType;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;
import static com.reason.bs.BsBinaries.getBsbPath;
import static com.reason.bs.BsBinaries.getBscPath;

public final class BsProcess implements CompilerProcess {

    private static final Pattern BS_VERSION_REGEXP = Pattern.compile(".*OCaml[:]?(\\d\\.\\d+.\\d+).+\\)");

    @NotNull
    private final Project m_project;

    @Nullable
    private BsProcessHandler m_bsb;
    //private RawProcessListener m_outputListener;

    private final AtomicBoolean m_started = new AtomicBoolean(false);
    private final AtomicBoolean m_restartNeeded = new AtomicBoolean(false);

    public BsProcess(@NotNull Project project) {
        m_project = project;
        create(Platform.findProjectBsconfig(project), CliType.make, null);
    }

    // Wait for the tool window to be ready before starting the process
    @Override
    public void startNotify() {
        if (m_bsb != null && !m_bsb.isStartNotified()) {
            try {
                m_bsb.startNotify();
            } catch (Throwable e) {
                // already done ?
            }
        }
    }

    private void create(@Nullable VirtualFile sourceFile, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            if (sourceFile != null) {
                createProcessHandler(sourceFile, cliType, onProcessTerminated);
            }
        } catch (ExecutionException e) {
            // Don't log when first time execution
        }
    }

    @Override
    public ProcessHandler recreate(@NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        throw new RuntimeException("Method not yet implemented.");
    }

    @Nullable
    public ProcessHandler recreate(@NotNull VirtualFile sourceFile, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            return createProcessHandler(sourceFile, cliType, onProcessTerminated);
        } catch (ExecutionException e) {
            Notifications.Bus.notify(new ORNotification("Bsb", "Can't run bsb\n" + e.getMessage(), NotificationType.ERROR));
        }

        return null;
    }

    @Nullable
    private ProcessHandler createProcessHandler(@NotNull VirtualFile sourceFile, @NotNull CliType cliType,
                                                @Nullable Compiler.ProcessTerminated onProcessTerminated) throws ExecutionException {
        killIt();
        GeneralCommandLine cli = getGeneralCommandLine(sourceFile, cliType);
        if (cli != null) {
            m_bsb = new BsProcessHandler(cli, onProcessTerminated);
            //if (m_outputListener == null) {
            addListener(new BsOutputListener(m_project, this));
            //} else {
            //    m_bsb.addRawProcessListener(m_outputListener);
            //}
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
        //m_outputListener = outputListener;
        if (m_bsb != null) {
            m_bsb.addRawProcessListener(outputListener);
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(@NotNull VirtualFile sourceFile, @NotNull CliType cliType) {
        String bsbPath = getBsbPath(m_project, sourceFile);
        if (bsbPath == null) {
            Notifications.Bus.notify(new ORNotification("Bsb", "<html>Can't find bsb.\n" + "The working directory is '" + ReasonSettings.getInstance(m_project)
                    .getWorkingDir(sourceFile) + "'.\n" + "Be sure that bsb is installed and reachable from that directory, "
                    + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#bucklescript\">github</a>.</html>", ERROR, URL_OPENING_LISTENER));
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

    @Override
    public boolean start() {
        boolean success = m_started.compareAndSet(false, true);
        if (!success) {
            m_restartNeeded.compareAndSet(false, true);
        }
        return success;
    }

    @Override
    public void terminate() {
        m_bsb = null;
        m_started.set(false);
    }

    @Nullable
    public String getOCamlVersion(@NotNull VirtualFile sourceFile) {
        String bsc = getBscPath(m_project, sourceFile);
        if (bsc != null) {
            Process p = null;
            try {
                p = Runtime.getRuntime().exec(bsc + " -version");
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                return line == null ? null : ocamlVersionExtractor(line);
            } catch (@NotNull InterruptedException | IOException e) {
                return null;
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }

        return null;
    }

    @Nullable
    static String ocamlVersionExtractor(@NotNull String line) {
        Matcher matcher = BS_VERSION_REGEXP.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return null;
    }
}
