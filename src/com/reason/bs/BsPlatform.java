package com.reason.bs;

import com.google.common.annotations.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.esy.*;
import com.reason.ide.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.Platform.*;
import static com.reason.bs.BsConstants.*;

public class BsPlatform {
    private static final Log LOG = Log.create("bs.platform");

    private BsPlatform() {
    }

    /**
     * Finds the "nearest" `bsconfig.json` to a given file. Searches up the file-system until a
     * `bsconfig.json` is found or the project root is reached.
     *
     * @param project    project to use
     * @param sourceFile starting point for search
     * @return `bsconfig.json` file, if found
     */
    public static @NotNull Optional<VirtualFile> findBsConfig(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return ORFileUtils.findAncestor(project, BS_CONFIG_FILENAME, sourceFile);
    }

    public static @NotNull Optional<VirtualFile> findContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile).map(VirtualFile::getParent);
    }

    public static @NotNull Optional<VirtualFile> findBsbExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile)
                .flatMap(bsConfig -> {
                    VirtualFile bsPlatform = findBsPlatformPathForConfigFile(project, bsConfig).orElse(null);
                    if (bsPlatform != null) {
                        return findBinaryInBsPlatform(BSB_EXE_NAME, bsPlatform);
                    } else {
                        // maybe a yarn v2 monorepo ? continue to parent to find a node_modules, unless root is reached
                        return ORFileUtils.findAncestor(project, NODE_MODULES, bsConfig.getParent()).
                                flatMap(nodeModules -> findBsPlatformPathInNodeModules(project, nodeModules).
                                        flatMap(bsPlatformDirectory -> findBinaryInBsPlatform(BSB_EXE_NAME, bsPlatformDirectory)));
                    }
                });
    }

    public static @NotNull Optional<VirtualFile> findBscExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile)
                .flatMap(bsContent -> {
                    VirtualFile bsPlatform = findBsPlatformPathForConfigFile(project, bsContent).orElse(null);
                    if (bsPlatform != null) {
                        return findBinaryInBsPlatform(BSC_EXE_NAME, bsPlatform);
                    } else {
                        // maybe a yarn v2 monorepo ? continue to parent to find a node_modules, unless root is reached
                        return ORFileUtils.findAncestor(project, NODE_MODULES, bsContent.getParent()).
                                flatMap(nodeModules -> findBsPlatformPathInNodeModules(project, nodeModules).
                                        flatMap(bsPlatformDirectory -> findBinaryInBsPlatform(BSC_EXE_NAME, bsPlatformDirectory)));
                    }
                });
    }

    public static Optional<VirtualFile> findEsyExecutable(@NotNull Project project) {
        String esyExecutable = ORSettings.getInstance(project).getEsyExecutable();
        if (esyExecutable.isEmpty()) {
            return Esy.findEsyExecutable();
        }
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByPath(esyExecutable));
    }

    public static Optional<VirtualFile> findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsPlatformDirectory = findBsConfig(project, sourceFile)
                .map(bsContent -> findBsPlatformPathForConfigFile(project, bsContent)
                        .orElseGet(() ->
                                // maybe a yarn v2 monorepo ? continue to parent to find a node_modules, unless root is reached
                                ORFileUtils.findAncestor(project, NODE_MODULES, bsContent.getParent())
                                        .flatMap(nodeModules -> findBsPlatformPathInNodeModules(project, nodeModules))
                                        .orElse(null))).orElse(null);
        if (bsPlatformDirectory == null) {
            return Optional.empty();
        }

        Optional<VirtualFile> binaryInBsPlatform;

        // first, try standard name
        binaryInBsPlatform = findBinaryInBsPlatform(REFMT_EXE_NAME, bsPlatformDirectory);
        if (!binaryInBsPlatform.isPresent()) {
            // next, try alternative names
            binaryInBsPlatform = findBinaryInBsPlatform("refmt3", bsPlatformDirectory);
            if (!binaryInBsPlatform.isPresent()) {
                binaryInBsPlatform = findBinaryInBsPlatform("bsrefmt", bsPlatformDirectory);
            }
        }

        return binaryInBsPlatform;
    }

    private static Optional<VirtualFile> findBsPlatformPathInNodeModules(@NotNull Project project, @NotNull VirtualFile nodeModules) {
        VirtualFile bsPlatform = nodeModules.findFileByRelativePath(BS_PLATFORM_NAME);
        if (bsPlatform == null) {
            VirtualFile bsbBinary = nodeModules.findFileByRelativePath(".bin/" + BSB_EXE_NAME);
            if (bsbBinary != null) {
                // This must be a mono-repo, only the .bin is found
                VirtualFile canonicalFile = bsbBinary.getCanonicalFile();
                if (canonicalFile != null) {
                    if (bsbBinary.is(VFileProperty.SYMLINK)) {
                        // Mac/Linux: .bin contains symlinks to real exe, must follow
                        VirtualFile canonicalBsPlatformDirectory = canonicalFile.getParent();
                        while (canonicalBsPlatformDirectory != null
                                && !canonicalBsPlatformDirectory.getName().equals(BS_PLATFORM_NAME)) {
                            canonicalBsPlatformDirectory = canonicalBsPlatformDirectory.getParent();
                        }
                        return Optional.ofNullable(canonicalBsPlatformDirectory);
                    } else {
                        // Windows: no symlinks, only bat files
                        return ORFileUtils.findAncestor(project, NODE_MODULES, nodeModules.getParent())
                                .map(nodeModules2 -> nodeModules2.findFileByRelativePath(BS_PLATFORM_NAME));
                    }
                }
            }
        }
        return Optional.ofNullable(bsPlatform).filter(VirtualFile::isDirectory);
    }

    private static Optional<VirtualFile> findBsPlatformPathForConfigFile(@NotNull Project project, @NotNull VirtualFile bsConfigFile) {
        VirtualFile parentDir = bsConfigFile.getParent();
        VirtualFile bsPlatform = parentDir.findFileByRelativePath("node_modules/" + BS_PLATFORM_NAME);
        if (bsPlatform == null) {
            VirtualFile bsbBinary = parentDir.findFileByRelativePath("node_modules/.bin/" + BSB_EXE_NAME);
            if (bsbBinary != null) {
                // This must be a mono-repo, only the .bin is found
                VirtualFile canonicalFile = bsbBinary.getCanonicalFile();
                if (canonicalFile != null) {
                    if (bsbBinary.is(VFileProperty.SYMLINK)) {
                        // Mac/Linux: .bin contains symlinks to real exe, must follow
                        VirtualFile canonicalBsPlatformDirectory = canonicalFile.getParent();
                        while (canonicalBsPlatformDirectory != null
                                && !canonicalBsPlatformDirectory.getName().equals(BS_PLATFORM_NAME)) {
                            canonicalBsPlatformDirectory = canonicalBsPlatformDirectory.getParent();
                        }
                        return Optional.ofNullable(canonicalBsPlatformDirectory);
                    } else {
                        // Windows: no symlinks, only bat files
                        return ORFileUtils.findAncestor(project, NODE_MODULES, parentDir.getParent())
                                .map(nodeModules -> nodeModules.findFileByRelativePath(BS_PLATFORM_NAME));
                    }
                }
            }
        }
        return Optional.ofNullable(bsPlatform).filter(VirtualFile::isDirectory);
    }

    private static Optional<VirtualFile> findBinaryInBsPlatform(@NotNull String executableName, @NotNull VirtualFile bsPlatformDirectory) {
        String platform = getOsBsPrefix();
        if (platform == null) {
            LOG.warn("Unable to determine OS prefix.");
            return Optional.empty();
        }

        VirtualFile executable;

        // first, try to find platform-specific binary
        executable = bsPlatformDirectory.findFileByRelativePath(platform + "/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
        if (executable == null) {
            // next, try to find platform-agnostic wrappers
            executable = bsPlatformDirectory.findFileByRelativePath(executableName + getOsBinaryWrapperExtension());
            if (executable == null) {
                // last, try old locations of binary
                executable = bsPlatformDirectory.findFileByRelativePath("bin/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
                if (executable == null) {
                    executable = bsPlatformDirectory.findFileByRelativePath("lib/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
                }
            }
        }

        return Optional.ofNullable(executable);
    }

    @VisibleForTesting
    static @NotNull String getOsBinaryWrapperExtension() {
        return SystemInfo.isWindows ? ".cmd" : "";
    }

    @VisibleForTesting
    static @Nullable String getOsBsPrefix() {
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
