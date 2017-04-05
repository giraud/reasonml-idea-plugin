package com.reason;

import com.intellij.openapi.diagnostic.Logger;

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

    // C:/sources/ReasonProject -> /mnt/sources/ReasonProject
    public static String toLinuxSubSystemPath(String filename) {
        return "/mnt/" + filename.substring(0, 1).toLowerCase() + filename.substring(2);
    }
}
