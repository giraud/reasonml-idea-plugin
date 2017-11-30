package com.reason;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Platform {

    private static Map<Project, VirtualFile> m_baseDirs = new HashMap<>();

    @NotNull
    public static String getBinary(String envVar, String propVar, @NotNull String defaultBinary) {
        Logger log = Logger.getInstance("ReasonML");
        log.info("Identifying '" + defaultBinary + "' binary");

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
    public static String getBinaryPath(Project project, String binary) {
        if (binary == null) {
            return null;
        }

        if (new File(binary).isAbsolute()) {
            return binary;
        }

        VirtualFile baseDir = Platform.findBaseRoot(project);
        VirtualFile absoluteBinary = baseDir.findFileByRelativePath(binary);

        return absoluteBinary == null ? null : absoluteBinary.getCanonicalPath();
    }

    public static String removeProjectDir(Project project, String path) {
        VirtualFile baseDir = Platform.findBaseRoot(project);
        return path.substring(baseDir.getPath().length());
    }

    public static String removeProjectDir(Project project, @Nullable VirtualFile file) {
        return file == null ? "" : removeProjectDir(project, file.getPath());
    }
}
