package com.reason.comp.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.notification.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.Compiler;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.intellij.notification.NotificationType.*;

public final class BsProcess {
    private final @NotNull Project myProject;
    private @Nullable KillableProcessHandler myBsb;

    public BsProcess(@NotNull Project project) {
        myProject = project;
    }

    @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
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
            myBsb = new BsColoredProcessHandler(cli, () -> {
                if (onProcessTerminated != null) {
                    onProcessTerminated.run();
                }

                // When build is done, we need to refresh editors to be notified of the latest modifications
                DaemonCodeAnalyzer.getInstance(myProject).restart();
            });
        }
        return myBsb;
    }

    private void killIt() {
        if (myBsb != null) {
            myBsb.killProcess();
            myBsb = null;
        }
    }

    @Nullable
    private GeneralCommandLine getGeneralCommandLine(@NotNull VirtualFile sourceFile, @NotNull CliType.Bs cliType) {
        Optional<VirtualFile> bsContentRootOptional = BsPlatform.findContentRoot(myProject, sourceFile);
        if (bsContentRootOptional.isEmpty()) {
            BsNotification.showWorkingDirectoryNotFound();
            return null;
        }
        String bsContentRoot = bsContentRootOptional.get().getPath();
        Optional<VirtualFile> bsbExecutable = BsPlatform.findBsbExecutable(myProject, sourceFile);
        if (bsbExecutable.isEmpty()) {
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

    public @NotNull String getFullVersion(@NotNull VirtualFile sourceFile) {
        VirtualFile bsc = BsPlatform.findBscExecutable(myProject, sourceFile);
        if (bsc != null) {
            try (InputStream inputStream = Runtime.getRuntime().exec(bsc.getPath() + " -version").getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.readLine();
            } catch (IOException e) {
                return "error: " + e.getMessage();
            }
        }

        return "unknown (bsc not found)";
    }
}
