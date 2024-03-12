package com.reason.hints;

import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.ide.hints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static jpsplugin.com.reason.Platform.*;

@Service(Service.Level.PROJECT)
public final class InsightManager {
    private static final Log LOG = Log.create("hints");

    final @NotNull AtomicBoolean isDownloading = new AtomicBoolean(false);
    private final @NotNull Project myProject;

    InsightManager(@NotNull Project project) {
        myProject = project;
    }

    public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
        VirtualFile parentFile = sourceFile.getParent();
        if (parentFile == null) {
            LOG.debug("Can't get parent file", sourceFile);
            return;
        }

        String rincewindName = ReadAction.compute(() -> getRincewindFilename(parentFile, ""));
        if (rincewindName == null) {
            LOG.debug("No rincewind version found, abort downloading");
            return;
        }

        File targetFile = getRincewindTarget(rincewindName);
        if (targetFile != null && !targetFile.exists()) {
            ProgressManager.getInstance().run(new RincewindDownloader(myProject, targetFile));
        }
    }

    public void queryTypes(@Nullable VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ORProcessTerminated<InferredTypes> runAfter) {
        VirtualFile sourceParentFile = sourceFile != null ? sourceFile.getParent() : null;
        String rincewindName = getRincewindFilename(sourceParentFile, "");
        File rincewindFile = getRincewindTarget(rincewindName);
        if (sourceFile != null && rincewindFile != null) {
            myProject.getService(RincewindProcess.class).types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
        }
    }

    public @NotNull List<String> dumpMeta(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "0.4");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpMeta(rincewindFile.getPath(), cmtFile);
    }

    public @NotNull String dumpTree(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? "<unknown>\n  <reason>rincewindFile not found</reason>\n  <file>" + cmtFile.getPath() + "</file>\n</unknown/>"
                : myProject.getService(RincewindProcess.class).dumpTree(cmtFile, rincewindFile.getPath());
    }

    public @NotNull List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile) {
        String rincewindName = getRincewindFilename(cmtFile.getParent(), "");
        File rincewindFile = rincewindName == null ? null : getRincewindTarget(rincewindName);
        return rincewindFile == null
                ? Collections.emptyList()
                : myProject.getService(RincewindProcess.class).dumpTypes(rincewindFile.getPath(), cmtFile);
    }

    @Nullable File getRincewindTarget(@Nullable String filename) {
        Path pluginLocation = getPluginLocation();
        String pluginPath = pluginLocation == null ? System.getProperty("java.io.tmpdir") : pluginLocation.toFile().getPath();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Rincewind filename: " + filename + " at " + pluginPath);
        }
        return filename == null ? null : new File(pluginPath, filename);
    }

    @Nullable String getRincewindFilename(@Nullable VirtualFile sourceFile, @NotNull String excludedVersion) {
        if (sourceFile != null) {
            ORCompilerManager compilerManager = myProject.getService(ORCompilerManager.class);
            ORResolvedCompiler<?> compiler = compilerManager.getCompiler(sourceFile);
            String fullVersion = compiler != null ? compiler.getFullVersion() : null;
            String ocamlVersion = Rincewind.extractOcamlVersion(fullVersion);
            String rincewindVersion = Rincewind.getLatestVersion(ocamlVersion);

            // ocaml version default - opam -> use ocaml -version ??
            // opam switch different from default
            // opam settings set correctly (not default)

            if (ocamlVersion != null && !rincewindVersion.equals(excludedVersion)) {
                return "rincewind_" + getOsPrefix() + ocamlVersion + "-" + rincewindVersion + ".exe";
            }
        }

        return null;
    }
}
