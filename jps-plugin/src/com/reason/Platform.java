package com.reason;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Platform {

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
            if (baseDir.findChild("package.json") == null) {
                // try to find it one level deeper
                baseDir = Arrays.stream(baseDir.getChildren()).filter(file -> file.findChild("package.json") != null).findFirst().orElse(baseDir);
            }
            m_baseDirs.put(project, baseDir);
        }
        return baseDir;
    }

    // Iterate through parents until a bsConfig.json is found - must not be called for an OCaml project
    @NotNull
    public static VirtualFile findBaseRootFromFile(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile baseDir = project.getBaseDir();
        if (sourceFile.equals(baseDir)) {
            return sourceFile;
        }

        VirtualFile parent = sourceFile.getParent();
        if (parent == null) {
            return sourceFile;
        }

        VirtualFile child = parent.findChild("bsconfig.json");
        while (child == null) {
            VirtualFile grandParent = parent.getParent();
            if (grandParent == null) {
                break;
            }

            parent = grandParent;
            child = parent.findChild("bsconfig.json");
            if (parent.equals(baseDir)) {
                break;
            }
        }

        return parent;
    }

    @Nullable
    public static String getBinaryPath(@NotNull Project project, @NotNull VirtualFile sourceFile, @Nullable String relativeBinaryPath) {
        if (relativeBinaryPath == null) {
            return null;
        }

        try {
            URI resource = new URI(relativeBinaryPath);
            if (resource.isAbsolute()) {
                String authority = resource.getAuthority();
                File file = new File((authority == null ? "" : authority) + resource.getPath());
                return file.exists() ? file.getPath() : null;
            }

            VirtualFile baseDir = Platform.findBaseRootFromFile(project, sourceFile);
            VirtualFile absoluteBinary = baseDir.findFileByRelativePath(relativeBinaryPath);

            return absoluteBinary == null ? null : absoluteBinary.getCanonicalPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @NotNull
    public static String removeProjectDir(@NotNull Project project, @NotNull String path) {
        try {
            VirtualFile baseRoot = Platform.findBaseRoot(project);
            Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
            Path relativePath = basePath.relativize(new File(path).toPath());
            return relativePath.toString();
        } catch (IllegalArgumentException e) {
            return path;
        }
    }

    @Nullable
    public static VirtualFile findBsConfigFromFile(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile baseRoot = Platform.findBaseRootFromFile(project, file);
        return baseRoot.findFileByRelativePath("bsconfig.json");
    }
}
