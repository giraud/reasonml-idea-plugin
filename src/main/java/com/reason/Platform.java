package com.reason;

import com.intellij.openapi.diagnostic.Logger;
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

    private static final Map<Project, VirtualFile> m_baseDirs = new HashMap<>();

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

    @Nullable
    public static String getBinary(String envVar, String propVar) {
        Logger log = Logger.getInstance("ReasonML");

        String binary = System.getProperty(propVar);
        if (binary != null) {
            log.info("Found '" + binary + "' in the property '" + propVar + "'");
            return binary;
        }

        log.info("Property '" + envVar + "' not found, testing environment variable '" + propVar + "'");
        binary = System.getenv(envVar);
        if (binary != null) {
            log.info("Found '" + binary + "' in the environment variable '" + envVar + "'");
            return binary;
        }

        return null;
    }

    @NotNull
    public static String getBinary(String envVar, String propVar, @NotNull String defaultBinary) {
        Logger log = Logger.getInstance("ReasonML");
        log.info("Identifying '" + defaultBinary + "' binary");

        String binary = getBinary(envVar, propVar);
        if (binary != null) {
            return binary;
        }

        log.warn("No '" + defaultBinary + "' found in environment or properties, use default one");
        return defaultBinary;
    }

    public static VirtualFile findBaseRoot(Project project) {
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
}
