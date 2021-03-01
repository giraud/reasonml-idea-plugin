package com.reason.bs;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.ide.annotations.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;

import static com.reason.Platform.*;
import static java.util.Collections.*;

public class BscProcess {

    private static final Log LOG = Log.create("process.bsc");

    private static final Duration TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);

    private final Project m_project;

    public static BscProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, BscProcess.class);
    }

    public BscProcess(Project project) {
        m_project = project;
    }

    @Nullable
    public Integer run(@NotNull VirtualFile sourceFile, @NotNull VirtualFile workDir, @NotNull List<String> arguments, @NotNull ProcessListener processListener) throws ORProcessException {
        Optional<VirtualFile> bscPath = BsPlatform.findBscExecutable(m_project, sourceFile);
        if (!bscPath.isPresent()) {
            LOG.info("Unable to find bsc.exe."); // not an error, don't want to generate popups
            return null;
        }

        List<String> command = new ArrayList<>();
        command.add(bscPath.get().getPath());
        command.addAll(arguments);
        GeneralCommandLine bscCli = new GeneralCommandLine(command)
                .withWorkDirectory(workDir.getPath())
                // disable coloring
                // https://rescript-lang.org/docs/manual/latest/build-configuration#error-output-coloring-ninja_ansi_forced
                .withEnvironment("NINJA_ANSI_FORCED", "0");
        if (LOG.isTraceEnabled()) {
            LOG.trace(bscPath.get().getPath() + " " + Joiner.join(" ", arguments));
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

    public @NotNull List<OutputInfo> exec(@NotNull VirtualFile sourceFile, @NotNull VirtualFile workDir, @NotNull List<String> arguments) {
        Optional<VirtualFile> bscPath = BsPlatform.findBscExecutable(m_project, sourceFile);
        if (!bscPath.isPresent()) {
            LOG.error("Unable to find bsc.exe.");
            return emptyList();
        }

        List<String> command = new ArrayList<>();
        command.add(bscPath.get().getPath());
        command.addAll(arguments);

        if (LOG.isTraceEnabled()) {
            LOG.trace(bscPath.get().getPath() + " " + Joiner.join(" ", arguments));
            LOG.trace("  work dir", workDir);
        }

        try {
            BsLineProcessor lineProcessor = new BsLineProcessor(LOG);
            AnsiEscapeDecoder m_ansiEscapeDecoder = new AnsiEscapeDecoder();

            Process exec = Runtime.getRuntime().exec(command.toArray(new String[0]), null, new File(workDir.getPath()));
            exec.waitFor(500, TimeUnit.MILLISECONDS);
            BufferedReader errReader = new BufferedReader(new InputStreamReader(exec.getErrorStream(), UTF8));
            errReader.lines().forEach(text -> {
                StringBuilder sb = new StringBuilder();
                m_ansiEscapeDecoder.escapeText(text, ProcessOutputType.STDERR, (chunk, attributes) -> sb.append(chunk));
                lineProcessor.onRawTextAvailable(sb.toString());
            });

            return lineProcessor.getInfo();
        } catch (InterruptedException | IOException e) {
            LOG.warn(e);
        }

        return emptyList();
    }
}
