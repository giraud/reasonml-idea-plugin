package com.reason.hints;

import com.intellij.openapi.application.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
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
    private static final Pattern BS_VERSION_REGEXP = Pattern.compile(".*OCaml[:]?(\\d\\.\\d+.\\d+).*\\)");

    final @NotNull AtomicBoolean isDownloading = new AtomicBoolean(false);
    private final @NotNull Project myProject;

    InsightManagerImpl(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
        String rincewindName = ReadAction.compute(() -> getRincewindFilename(sourceFile.getParent(), ""));
        if (rincewindName == null) {
            LOG.debug("No rincewind version found, abort downloading");
            return;
        }

        File targetFile = getRincewindTarget(rincewindName);
        if (targetFile != null && !targetFile.exists()) {
            ProgressManager.getInstance().run(new RincewindDownloader(myProject, targetFile));
        }
    }

    @Override
    public void queryTypes(@NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
        String rincewindName = getRincewindFilename(sourceFile.getParent(), "");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        if (rincewindFile != null) {
            myProject.getService(RincewindProcess.class).types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
        }
    }

    @Override
    public @NotNull List<String> dumpMeta(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "0.4");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpMeta(rincewindFile.getPath(), cmtFile);
    }

    @Override
    public @NotNull String dumpTree(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? "<unknown>\n  <reason>rincewindFile not found</reason>\n  <file>" + cmtFile.getPath() + "</file>\n</unknow/>"
                : myProject.getService(RincewindProcess.class).dumpTree(cmtFile, rincewindFile.getPath());
    }

    @Override
    public @NotNull List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpTypes(rincewindFile.getPath(), cmtFile);
    }

    public @Nullable File getRincewindTarget(@NotNull String filename) {
        Path pluginLocation = Platform.getPluginLocation();
        String pluginPath = pluginLocation == null ? System.getProperty("java.io.tmpdir") : pluginLocation.toFile().getPath();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Rincewind filename: " + filename + " at " + pluginPath);
        }
        return new File(pluginPath, filename);
    }

    public @Nullable String getRincewindFilename(@NotNull VirtualFile sourceFile, @NotNull String excludedVersion) {
        ORCompilerManager compilerManager = myProject.getService(ORCompilerManager.class);
        ORResolvedCompiler<?> compiler = compilerManager.getCompiler(sourceFile);
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
