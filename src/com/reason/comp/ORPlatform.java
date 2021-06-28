package com.reason.comp;

import com.google.common.annotations.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import static com.reason.Platform.*;
import static com.reason.comp.ORConstants.*;

public class ORPlatform {
    private static final Log LOG = Log.create("platform");

    public static @Nullable VirtualFile findCompilerPathInNodeModules(@NotNull Project project, @NotNull VirtualFile configFile, @NotNull String dirName, @NotNull String binName) {
        VirtualFile parentDir = configFile.getParent();
        VirtualFile nodeModules = parentDir == null ? null : parentDir.findFileByRelativePath(NODE_MODULES);
        if (parentDir != null && nodeModules == null) {
            // In yarn workspaces, node_modules can be in a parent directory
            nodeModules = ORFileUtils.findAncestor(project, NODE_MODULES, parentDir);
        }

        VirtualFile binRootDir = nodeModules == null ? null : nodeModules.findFileByRelativePath(dirName);
        if (nodeModules != null && binRootDir == null) {
            VirtualFile binary = nodeModules.findFileByRelativePath(".bin/" + binName);
            if (binary != null) {
                // This must be a mono-repo, only the .bin is found
                VirtualFile canonicalFile = binary.getCanonicalFile();
                if (canonicalFile != null) {
                    if (binary.is(VFileProperty.SYMLINK)) {
                        // Mac/Linux: .bin contains symlinks to real exe, must follow
                        VirtualFile canonicalDirectory = canonicalFile.getParent();
                        while (canonicalDirectory != null && !dirName.equals(canonicalDirectory.getName())) {
                            canonicalDirectory = canonicalDirectory.getParent();
                        }
                        return canonicalDirectory;
                    } else {
                        // Windows: no symlinks, only bat files
                        VirtualFile nodModules = ORFileUtils.findAncestor(project, NODE_MODULES, nodeModules.getParent());
                        return nodModules == null ? null : nodeModules.findFileByRelativePath(dirName);
                    }
                }
            }
        }

        return binRootDir != null && binRootDir.isDirectory() ? binRootDir : null;
    }


    public static @Nullable VirtualFile findBinary(@NotNull VirtualFile binDir, @NotNull String exeName) {
        String os = getOsPrefix();
        if (os == null) {
            LOG.warn("Unable to determine OS prefix");
            return null;
        }

        VirtualFile executable;

        // first, try to find platform-specific binary
        executable = binDir.findFileByRelativePath(os + "/" + exeName + WINDOWS_EXECUTABLE_SUFFIX);
        if (executable == null) {
            // next, try to find platform-agnostic wrappers
            executable = binDir.findFileByRelativePath(exeName + getOsBinaryWrapperExtension());
            if (executable == null) {
                // last, try old locations of binary
                executable = binDir.findFileByRelativePath("bin/" + exeName + WINDOWS_EXECUTABLE_SUFFIX);
                if (executable == null) {
                    executable = binDir.findFileByRelativePath("lib/" + exeName + WINDOWS_EXECUTABLE_SUFFIX);
                }
            }
        }

        return executable;
    }

    @VisibleForTesting
    static @NotNull String getOsBinaryWrapperExtension() {
        return SystemInfo.isWindows ? ".cmd" : "";
    }

    @VisibleForTesting
    static @Nullable String getOsPrefix() {
        if (SystemInfo.isWindows) {
            return "win32";
        }
        if (SystemInfo.isLinux) {
            return "linux";
        }
        if (SystemInfo.isMac) {
            return "darwin";
        }
        return null;
    }
}
