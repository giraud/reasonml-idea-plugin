package com.reason.comp.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.ide.annotations.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;

import static jpsplugin.com.reason.Platform.*;
import static java.util.Collections.*;

public class BscProcess {
    private static final Log LOG = Log.create("process.bsc");
    private static final Duration TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

    private final Project myProject;

    public BscProcess(Project project) {
        myProject = project;
    }

    @Nullable
    public Integer run(@NotNull VirtualFile sourceFile, @NotNull VirtualFile workDir, @NotNull List<String> arguments, @NotNull ProcessListener processListener) throws ORProcessException {
        VirtualFile bscPath = BsPlatform.findBscExecutable(myProject, sourceFile);
        if (bscPath == null) {
            LOG.info("Unable to find bsc.exe."); // not an error, don't want to generate popups
            return null;
        }

        List<String> command = new ArrayList<>();
        command.add(bscPath.getPath());
        command.addAll(arguments);
        GeneralCommandLine bscCli = new GeneralCommandLine(command)
                .withWorkDirectory(workDir.getPath())
                // disable coloring
                // https://rescript-lang.org/docs/manual/latest/build-configuration#error-output-coloring-ninja_ansi_forced
                .withEnvironment("NINJA_ANSI_FORCED", "0");
        if (LOG.isTraceEnabled()) {
            LOG.trace(bscPath.getPath() + " " + Joiner.join(" ", arguments));
            LOG.trace("  work dir", workDir);
        }

        OSProcessHandler bscProcessHandler;
        try {
            bscProcessHandler = new KillableProcessHandler(bscCli);
        } catch (ExecutionException e) {
            throw new ORProcessException(e.getMessage());
        }

        bscProcessHandler.addProcessListener(processListener);
        bscProcessHandler.startNotify();
        bscProcessHandler.waitFor(TIMEOUT.toMillis());
        return bscProcessHandler.getExitCode();
    }
}
