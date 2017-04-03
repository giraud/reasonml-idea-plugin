package com.reason;

import java.util.Locale;

public class Platform {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("windows");
    }

    public static String getExtension() {
        return isWindows() ? ".bat" : "";
    }
}
