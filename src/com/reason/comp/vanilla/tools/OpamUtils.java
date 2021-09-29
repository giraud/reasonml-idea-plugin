package com.reason.comp.vanilla.tools;

import com.intellij.openapi.util.io.*;
import com.reason.ide.sdk.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utilities for .opam kind of SDK.
 * "extended" by PortableOpamUtils.
 */
public class OpamUtils {

    /** true if it is an opam SDK **/
    public static boolean isOpam(@NotNull File sdkHome) {
        return isOpamPath(sdkHome.getPath());
    }
    public static boolean isOpamPath(@NotNull String sdkHomePath) {
        return sdkHomePath.contains(".opam");
    }

    /** the folder in which sources are stored for opam SDKs,
     * @implNote we suppose that the sdk is an opam SDK */
    public static String getOpamSDKSourceFolder(@NotNull File sdkHome, @NotNull String version) {
        return new File(
                sdkHome,
                ".opam-switch/sources/ocaml-base-compiler." + version
        ).getAbsolutePath();
    }

    /** Check if we have sdk in the usual opam folder */
    public static void lookForSDK(HashSet<Path> roots) {
        File opamFolder = new File(FileUtil.expandUserHome("~/.opam/home/"));
        if (opamFolder.exists() && opamFolder.isDirectory()) {
            exploreOpamFolder(opamFolder, roots);
        }
    }

    private static void exploreOpamFolder(File folder, HashSet<Path> roots) {
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (!f.canRead() || f.isFile()) {
                continue;
            }
            if (OCamlSdkType.VERSION_REGEXP.matcher(f.getPath()).matches()) {
                roots.add(Path.of(f.getPath()));
            }
        }
    }
}