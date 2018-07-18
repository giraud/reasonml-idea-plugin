package com.reason;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Platform {

    public static final String OCAML_VERSION = "4.02";
    public static final String RINCEWIND_VERSION = "0.3";

    private static final Map<Project, VirtualFile> m_baseDirs = new HashMap<>();

    private Platform() {
    }

    @NotNull
    public static String getOsPrefix() {
        if (SystemInfo.isWindows) {
            return "w";
        }

        if (SystemInfo.isLinux) {
            return "l";
        }

        if (SystemInfo.isMac) {
            return "o";
        }

        return "";
    }

    @NotNull
    public static VirtualFile findBaseRoot(@NotNull Project project) {
        VirtualFile baseDir = m_baseDirs.get(project);
        if (baseDir == null) {
            baseDir = project.getBaseDir();
            if (baseDir.findChild("node_modules") == null) {
                // try to find it one level deeper
                baseDir = Arrays.stream(baseDir.getChildren()).filter(file -> file.findChild("node_modules") != null).findFirst().orElse(baseDir);
            }
            m_baseDirs.put(project, baseDir);
        }
        return baseDir;
    }

    // Iterate through parents until a bsConfig.json is found - must not be called for an OCaml project
    @NotNull
    public static VirtualFile findBaseRootFromFile(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile baseDir = project.getBaseDir();

        VirtualFile parent = sourceFile.getParent();
        VirtualFile child = parent.findChild("bsconfig.json");
        while (child == null) {
            parent = parent.getParent();
            child = parent.findChild("bsconfig.json");
            if (parent.equals(baseDir)) {
                throw new RuntimeException("problem when trying to find bsconfig.json, nothing found for " + sourceFile);
            }
        }

        return parent;
    }

    @Nullable
    public static String getBinaryPath(@NotNull Project project, @Nullable String binary) {
        if (binary == null) {
            return null;
        }

        File file = new File(binary);
        if (file.isAbsolute()) {
            return file.exists() ? binary : null;
        }

        VirtualFile baseDir = Platform.findBaseRoot(project);
        VirtualFile absoluteBinary = baseDir.findFileByRelativePath(binary);

        return absoluteBinary == null ? null : absoluteBinary.getCanonicalPath();
    }

    @NotNull
    private static String removeProjectDir(Project project, String path) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativize = basePath.relativize(new File(path).toPath());
        return relativize.toString();
    }

    @NotNull
    public static String removeProjectDir(Project project, @Nullable VirtualFile file) {
        return file == null ? "" : removeProjectDir(project, file.getPath());
    }

    @Nullable
    public static VirtualFile findBsConfigFromFile(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile baseRoot = Platform.findBaseRootFromFile(project, file);
        return baseRoot.findFileByRelativePath("bsconfig.json");
    }
}
