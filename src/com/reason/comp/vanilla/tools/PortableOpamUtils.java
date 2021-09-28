package com.reason.comp.vanilla.tools;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.*;
import com.reason.ide.sdk.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Windows users may use my portable version of opam,
 * that they will download automatically if they select the
 * "yet to come" feature "download SDK".
 *
 * Example: the files for the SDK 4.11.1 (for OCaml 4.11.1) will be in
 * ~/.opam/user/portable/4.11.1.
 */
public class PortableOpamUtils {

    private static final String PORTABLE_OPAM_RELATIVE_PATH = "usr\\portable";
    private static final String PORTABLE_OPAM_PATH = "~\\.opam\\" + PORTABLE_OPAM_RELATIVE_PATH;
    private static final String OPAM_BIN_FOLDER = "~\\.opam\\bin";

    // methods

    public static boolean isPortableOpam(String sdkHome) {
        return SystemInfo.isWindows && sdkHome.contains(PORTABLE_OPAM_RELATIVE_PATH);
    }

    public static GeneralCommandLine makeGeneralCommandLine(@NonNls @NotNull List<String> args) {
        return new CustomCommandLine(args);
    }

    public static void lookForSDK(HashSet<Path> roots) {
        File opamFolderWindows = new File(FileUtil.expandUserHome(PORTABLE_OPAM_PATH));
        if (opamFolderWindows.exists()) {
            for (File f : Objects.requireNonNull(opamFolderWindows.listFiles())) {
                if (!f.canRead() || f.isFile()) {
                    continue;
                }
                final String path = f.getPath().replace("\\", "/");
                if (OCamlSdkType.VERSION_REGEXP.matcher(path).matches()) {
                    roots.add(Path.of(f.getPath()));
                }
            }
        }
    }

    // classes

    private static final class CustomCommandLine extends GeneralCommandLine {
        public CustomCommandLine(@NonNls @NotNull List<String> command) {
            super(command);
        }

        @Override protected void setupEnvironment(@NotNull Map<String, String> environment) {
            super.setupEnvironment(environment);
            // replace PATH, because we shouldn't use Windows commands
            // as they are not working with the portable opam
            environment.put("PATH", FileUtil.expandUserHome(OPAM_BIN_FOLDER));
        }
    }

}