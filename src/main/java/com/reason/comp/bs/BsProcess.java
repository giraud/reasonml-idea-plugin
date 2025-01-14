package com.reason.comp.bs;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.notification.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static com.intellij.notification.NotificationType.*;

public final class BsProcess {
    private final @NotNull Project myProject;
    private @Nullable KillableProcessHandler myBsb;

    public BsProcess(@NotNull Project project) {
        myProject = project;
    }

    @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) {
        try {
            if (cliType instanceof CliType.Bs) {
                return createProcessHandler(source, (CliType.Bs) cliType, onProcessTerminated);
            } else {
                Notifications.Bus.notify(new ORNotification("Bsb", "Invalid commandline type (" + cliType.getCompilerType() + ")", WARNING));
            }
        } catch (ExecutionException e) {
            ORNotification.notifyError("Bsb", "Execution exception", e.getMessage());
        }

        return null;
    }

    @Nullable
    private ProcessHandler createProcessHandler(@NotNull VirtualFile sourceFile, @NotNull CliType.Bs cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated) throws ExecutionException {
        killIt();
        GeneralCommandLine cli = getGeneralCommandLine(sourceFile, cliType);
        if (cli != null) {
            myBsb = new BsColoredProcessHandler(cli, (_none) -> {
                if (onProcessTerminated != null) {
                    onProcessTerminated.run(null);
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
        VirtualFile bsConfigFile = BsPlatform.findConfigFile(myProject, sourceFile);
        VirtualFile bsConfigDir = bsConfigFile != null ? bsConfigFile.getParent() : null;
        if (bsConfigDir == null) {
            BsNotification.showWorkingDirectoryNotFound();
            return null;
        }
        String bsConfigRootPath = bsConfigDir.getPath();
        VirtualFile bsbExecutable = BsPlatform.findBsbExecutable(myProject, sourceFile);
        if (bsbExecutable == null) {
            BsNotification.showBsbNotFound(bsConfigRootPath);
            return null;
        }
        String bsbPath = bsbExecutable.getPath();
        GeneralCommandLine cli = switch (cliType) {
            case MAKE -> new GeneralCommandLine(bsbPath, "-make-world");
            case CLEAN_MAKE -> new GeneralCommandLine(bsbPath, "-clean-world", "-make-world");
        };
        cli.withWorkDirectory(bsConfigRootPath);
        cli.withEnvironment("NINJA_ANSI_FORCED", "1");
        return cli;
    }

    public @NotNull String getFullVersion(@Nullable VirtualFile sourceFile) {
        VirtualFile bsc = BsPlatform.findBscExecutable(myProject, sourceFile);
        if (bsc != null) {
            String[] command = new String[]{bsc.getPath(), "-version"};
            try (InputStream inputStream = Runtime.getRuntime().exec(command).getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return reader.readLine();
            } catch (IOException e) {
                return "error: " + e.getMessage();
            }
        }

        return "unknown (bsc not found)";
    }
}
