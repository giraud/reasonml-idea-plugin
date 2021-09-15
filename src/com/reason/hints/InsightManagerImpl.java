package com.reason.hints;

import com.intellij.openapi.components.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import static jpsplugin.com.reason.Platform.*;

public class InsightManagerImpl implements InsightManager {
    private static final Log LOG = Log.create("hints");
    private static final Pattern BS_VERSION_REGEXP = Pattern.compile(".*OCaml[:]?(\\d\\.\\d+.\\d+).+\\)");

    final @NotNull AtomicBoolean isDownloading = new AtomicBoolean(false);
    private final @NotNull Project myProject;

    InsightManagerImpl(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
        File rincewind = getRincewindFile(sourceFile);
        if (rincewind == null || !rincewind.exists()) {
            ProgressManager.getInstance().run(new RincewindDownloader(myProject, sourceFile));
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
        File rincewindFile = getRincewindFile(sourceFile);
        if (rincewindFile != null) {
            myProject.getService(RincewindProcess.class).types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
        }
    }

    @Override
    public @NotNull List<String> dumpMeta(@NotNull VirtualFile cmtFile) {

        File rincewindFile = getRincewindFileExcludingVersion(cmtFile, "0.4");
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpMeta(rincewindFile.getPath(), cmtFile);
    }

    @Override
    public @NotNull String dumpTree(@NotNull VirtualFile cmtFile) {
        File rincewindFile = getRincewindFile(cmtFile);
        return rincewindFile == null
                ? "<unknown/>"
                : myProject.getService(RincewindProcess.class).dumpTree(cmtFile, rincewindFile.getPath());
    }

    @Override
    public @NotNull List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile) {
        File rincewindFile = getRincewindFile(cmtFile);
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpTypes(rincewindFile.getPath(), cmtFile);
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
        ORCompilerManager compilerManager = myProject.getService(ORCompilerManager.class);
        Compiler compiler = compilerManager.getCompiler(sourceFile);
        String fullVersion = compiler == null ? null : compiler.getFullVersion(sourceFile);
        String ocamlVersion = ocamlVersionExtractor(fullVersion);
        String rincewindVersion = getRincewindVersion(ocamlVersion);

        if (ocamlVersion != null && !rincewindVersion.equals(excludedVersion)) {
            return "rincewind_" + getOsPrefix() + ocamlVersion + "-" + rincewindVersion + ".exe";
        }

        return null;
    }

    static @Nullable String ocamlVersionExtractor(@Nullable String fullVersion) {
        if (fullVersion != null) {
            Matcher matcher = BS_VERSION_REGEXP.matcher(fullVersion);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            if (fullVersion.startsWith("ReScript")) {
                return "4.06.1";
            }
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
