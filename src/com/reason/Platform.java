package com.reason;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;
import java.util.Locale;

public class Platform {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows");
    }

    public static String getExtension() {
        return isWindows() ? ".bat" : "";
    }

    public static String getBinary(String envVar, String propVar, String defaultBinary) {
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
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir.findChild("node_modules") == null) {
            // try to find it one level deeper
            return Arrays.stream(baseDir.getChildren()).filter(file -> file.findChild("node_modules") != null).findFirst().orElse(baseDir);
        }
        return baseDir;
    }

}
