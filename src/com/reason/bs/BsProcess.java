package com.reason.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.notification.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.Compiler;
import com.reason.*;
import com.reason.ide.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import static com.intellij.notification.NotificationType.*;
import static com.reason.bs.BsPlatform.*;

public final class BsProcess implements CompilerProcess {
    private static final Pattern BS_VERSION_REGEXP = Pattern.compile(".*OCaml[:]?(\\d\\.\\d+.\\d+).+\\)");

    private final AtomicBoolean m_started = new AtomicBoolean(false);
    private final AtomicBoolean m_restartNeeded = new AtomicBoolean(false);
    private final @NotNull Project m_project;
    private @Nullable KillableProcessHandler m_bsb;

    public BsProcess(@NotNull Project project) {
        this.m_project = project;
        // no file is active yet, default working directory to the top-level bsconfig.json file
        VirtualFile firstBsContentRoot = ORProjectManager.findFirstBsConfigurationFile(project).orElse(null);
        create(firstBsContentRoot);
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

    private void create(@Nullable VirtualFile sourceFile) {
        try {
            if (sourceFile != null) {
                createProcessHandler(sourceFile, CliType.Bs.MAKE, null);
            }
        } catch (ExecutionException e) {
            // Don't log when first time execution
        }
    }

    @Override
    public @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
        try {
            if (cliType instanceof CliType.Bs) {
                return createProcessHandler(source, (CliType.Bs) cliType, onProcessTerminated);
            } else {
                Notifications.Bus.notify(new ORNotification("Bsb", "Invalid commandline type (" + cliType.getCompilerType() + ")", WARNING));
            }
        } catch (ExecutionException e) {
            ORNotification.notifyError("Bsb", "Execution exception", e.getMessage(), null);
        }

        return null;
    }

    @Nullable
    private ProcessHandler createProcessHandler(@NotNull VirtualFile sourceFile, @NotNull CliType.Bs cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) throws ExecutionException {
        killIt();
        GeneralCommandLine cli = getGeneralCommandLine(sourceFile, cliType);
        if (cli != null) {
            m_bsb = new BsColoredProcessHandler(cli, () -> {
                terminate();
                if (onProcessTerminated != null) {
                    onProcessTerminated.run();
                }

                // When build is done, we need to refresh editors to be notified of the latest modifications
                DaemonCodeAnalyzer.getInstance(m_project).restart();
            });
        }
        return m_bsb;
    }

    private void killIt() {
        if (m_bsb != null) {
            m_bsb.killProcess();
            m_bsb = null;
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(@NotNull VirtualFile sourceFile, @NotNull CliType.Bs cliType) {
        Optional<VirtualFile> bsContentRootOptional = BsPlatform.findContentRoot(m_project, sourceFile);
        if (!bsContentRootOptional.isPresent()) {
            BsNotification.showWorkingDirectoryNotFound();
            return null;
        }
        String bsContentRoot = bsContentRootOptional.get().getPath();
        Optional<VirtualFile> bsbExecutable = findBsbExecutable(m_project, sourceFile);
        if (!bsbExecutable.isPresent()) {
            BsNotification.showBsbNotFound(bsContentRoot);
            return null;
        }
        String bsbPath = bsbExecutable.get().getPath();
        GeneralCommandLine cli;
        switch (cliType) {
            case MAKE:
                cli = new GeneralCommandLine(bsbPath, "-make-world");
                break;
            case CLEAN_MAKE:
                cli = new GeneralCommandLine(bsbPath, "-clean-world", "-make-world");
                break;
            default:
                cli = new GeneralCommandLine(bsbPath);
        }
        cli.withWorkDirectory(bsContentRoot);
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
        return findBscExecutable(m_project, sourceFile)
                .map(bscFile -> {
                    String bscExe = bscFile.getPath();
                    Process p = null;
                    try {
                        p = Runtime.getRuntime().exec(bscExe + " -version");
                        p.waitFor();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        return ocamlVersionExtractor(reader.readLine());
                    } catch (InterruptedException | IOException e) {
                        return null;
                    } finally {
                        if (p != null) {
                            p.destroy();
                        }
                    }
                })
                .orElse(null);
    }

    static @Nullable String ocamlVersionExtractor(@Nullable String line) {
        if (line != null) {
            Matcher matcher = BS_VERSION_REGEXP.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }
}
