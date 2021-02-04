package com.reason.hints;

import com.intellij.openapi.components.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.bs.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static com.reason.Platform.*;

public class InsightManagerImpl implements InsightManager {
    private static final Log LOG = Log.create("hints");

    final @NotNull AtomicBoolean isDownloading = new AtomicBoolean(false);
    private final @NotNull Project m_project;

    private InsightManagerImpl(@NotNull Project project) {
        m_project = project;
    }

    @Override
    public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
        File rincewind = getRincewindFile(sourceFile);
        if (rincewind == null || !rincewind.exists()) {
            ProgressManager.getInstance().run(new RincewindDownloader(m_project, sourceFile));
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
        File rincewindFile = getRincewindFile(sourceFile);
        if (rincewindFile != null) {
            ServiceManager.getService(m_project, RincewindProcess.class).types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
        }
    }

    @Override
    public @NotNull List<String> dumpMeta(@NotNull VirtualFile cmtFile) {

        File rincewindFile = getRincewindFileExcludingVersion(cmtFile, "0.4");
        return rincewindFile == null
                ? Collections.emptyList()
                : ServiceManager.getService(m_project, RincewindProcess.class).dumpMeta(rincewindFile.getPath(), cmtFile);
    }

    @Override
    public @NotNull String dumpTree(@NotNull VirtualFile cmtFile) {
        File rincewindFile = getRincewindFile(cmtFile);
        return rincewindFile == null
                ? "<unknown/>"
                : ServiceManager.getService(m_project, RincewindProcess.class).dumpTree(cmtFile, rincewindFile.getPath());
    }

    @Override
    public @NotNull List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile) {
        File rincewindFile = getRincewindFile(cmtFile);
        return rincewindFile == null
                ? Collections.emptyList()
                : ServiceManager.getService(m_project, RincewindProcess.class).dumpTypes(rincewindFile.getPath(), cmtFile);
    }

    @Override
    public @Nullable File getRincewindFile(@NotNull VirtualFile sourceFile) {
        return getRincewindFileExcludingVersion(sourceFile, "");
    }

    public @Nullable File getRincewindFileExcludingVersion(@NotNull VirtualFile sourceFile, @NotNull String excludedVersion) {
        String filename = getRincewindFilenameExcludingVersion(sourceFile, excludedVersion);
        if (filename == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No rincewind file found for " + sourceFile + " (excluded: " + excludedVersion + ")");
            }
            return null;
        }

        Path pluginLocation = Platform.getPluginLocation();
        String pluginPath = pluginLocation == null ? System.getProperty("java.io.tmpdir") : pluginLocation.toFile().getPath();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Rincewind filename: " + filename + " at " + pluginPath);
        }
        return new File(pluginPath, filename);
    }

    @Override
    public @Nullable String getRincewindFilename(@NotNull VirtualFile sourceFile) {
        return getRincewindFilenameExcludingVersion(sourceFile, "");
    }

    public @Nullable String getRincewindFilenameExcludingVersion(@NotNull VirtualFile sourceFile, @NotNull String excludedVersion) {
        String ocamlVersion =
                ServiceManager.getService(m_project, BsProcess.class).getOCamlVersion(sourceFile);
        String rincewindVersion = getRincewindVersion(ocamlVersion);

        if (ocamlVersion != null && !rincewindVersion.equals(excludedVersion)) {
            return "rincewind_" + getOsPrefix() + ocamlVersion + "-" + rincewindVersion + ".exe";
        }

        return null;
    }

    private @NotNull String getRincewindVersion(@Nullable String ocamlVersion) {
        if ("4.02".equals(ocamlVersion)) {
            return "0.4";
        }

        return "0.9.1";
    }
}
