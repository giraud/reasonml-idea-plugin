package com.reason.bs;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reason.Log;
import com.reason.ide.ORFileUtils;
import com.reason.ide.ORProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.reason.Platform.WINDOWS_EXECUTABLE_SUFFIX;
import static com.reason.bs.BsConstants.*;

public class BsPlatform {

    private static final Log LOG = Log.create("bs.platform");

    private BsPlatform() {
    }

    public static Optional<VirtualFile> findFirstBsPlatformDirectory(@NotNull Project project) {
        return ORProjectManager.findFirstBsContentRoot(project)
                .flatMap(BsPlatform::findBsPlatformPathForConfigFile);
    }

    /**
     * Find `bs-platform` directory.
     * Given a `sourceFile`, searches from that file's location for a `bsconfig.json`
     * file. If found, then checks for a `./node_modules/bs-platform` directory relative to the `bsconfig.json`.
     *
     * @param project
     * @param sourceFile starting location for search
     * @return `bs-platform` directory, if found
     */
    public static Optional<VirtualFile> findBsPlatformDirectory(@NotNull Project project,
                                                                @NotNull VirtualFile sourceFile) {
        return findBsConfigForFile(project, sourceFile)
                .flatMap(BsPlatform::findBsPlatformPathForConfigFile);
    }

    public static Optional<VirtualFile> findBsbExecutable(@NotNull VirtualFile bsPlatformDirectory) {
        return findBinaryInBsPlatform(BSB_EXECUTABLE_NAME, bsPlatformDirectory);
    }

    public static Optional<VirtualFile> findBscExecutable(@NotNull VirtualFile bsPlatformDirectory) {
        return findBinaryInBsPlatform(BSC_EXECUTABLE_NAME, bsPlatformDirectory);
    }

    public static Optional<VirtualFile> findContentRootForFile(@NotNull Project project,
                                                               @NotNull VirtualFile sourceFile) {
        return findBsConfigForFile(project, sourceFile).map(VirtualFile::getParent);
    }

    /**
     * Finds the "nearest" `bsconfig.json` to a given file. Searches up the file-system until a `bsconfig.json`
     * is found or the project root is reached.
     *
     * @param project
     * @param sourceFile starting point for search
     * @return `bsconfig.json` file, if found
     */
    public static Optional<VirtualFile> findBsConfigForFile(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return ORFileUtils.findAncestorRecursive(project, BS_CONFIG_FILENAME, sourceFile);
    }

    public static Optional<VirtualFile> findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        Optional<VirtualFile> bsPlatformDirectoryOptional = findBsPlatformDirectory(project, sourceFile);
        if (!bsPlatformDirectoryOptional.isPresent()) {
            return Optional.empty();
        }
        VirtualFile bsPlatformDirectory = bsPlatformDirectoryOptional.get();
        Optional<VirtualFile> binaryInBsPlatform;

        // first, try standard name
        binaryInBsPlatform = findBinaryInBsPlatform(REFMT_EXECUTABLE_NAME, bsPlatformDirectory);
        if (binaryInBsPlatform.isPresent()) {
            return binaryInBsPlatform;
        }
        // next, try alternative names
        binaryInBsPlatform = findBinaryInBsPlatform("refmt3", bsPlatformDirectory);
        if (binaryInBsPlatform.isPresent()) {
            return binaryInBsPlatform;
        }
        return findBinaryInBsPlatform("bsrefmt", bsPlatformDirectory);
    }

    private static Optional<VirtualFile> findBsPlatformPathForConfigFile(@NotNull VirtualFile bsConfigFile) {
        VirtualFile parentDir = bsConfigFile.getParent();
        VirtualFile bsPlatform = parentDir.findFileByRelativePath("node_modules/" + BS_PLATFORM_DIRECTORY_NAME);
        if (bsPlatform == null) {
            bsPlatform = parentDir.findFileByRelativePath("node_modules/.bin"); // In case of mono-repo, only the .bin with symlinks is found
        }
        return Optional.ofNullable(bsPlatform).filter(VirtualFile::isDirectory);
    }

    private static Optional<VirtualFile> findBinaryInBsPlatform(@NotNull String executableName,
                                                                @NotNull VirtualFile bsPlatformDirectory) {
        Optional<String> platform = getOsBsPrefix();
        if (!platform.isPresent()) {
            LOG.warn("Unable to determine OS prefix.");
            return Optional.empty();
        }
        VirtualFile executable;
        // first, try to find platform-specific binary
        executable = bsPlatformDirectory.findFileByRelativePath(platform.get() + "/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
        if (executable != null) {
            return Optional.of(executable);
        }
        // next, try to find platform-agnostic wrappers / symlinks
        executable = bsPlatformDirectory.findFileByRelativePath(executableName + getOsBinaryWrapperExtension());
        if (executable != null) {
            if (executable.is(VFileProperty.SYMLINK)) {
                // a symlink references the node wrapper, so we need to follow it and try to resolve the native binary
                VirtualFile canonicalFile = executable.getCanonicalFile();
                if (canonicalFile != null) {
                    String canonicalPath = canonicalFile.getPath();
                    VirtualFile canonicalExecutable = VirtualFileManager.getInstance().findFileByUrl("file://" + canonicalPath + WINDOWS_EXECUTABLE_SUFFIX);
                    return Optional.of(canonicalExecutable == null ? canonicalFile : canonicalExecutable);
                }
            }
            return Optional.of(executable);
        }
        // last, try old locations of binary
        executable = bsPlatformDirectory.findFileByRelativePath("bin/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
        if (executable != null) {
            return Optional.of(executable);
        }
        executable = bsPlatformDirectory.findFileByRelativePath("lib/" + executableName + WINDOWS_EXECUTABLE_SUFFIX);
        if (executable != null) {
            return Optional.of(executable);
        }
        executable = bsPlatformDirectory.findFileByRelativePath("lib/" + executableName + ".exe");
        return Optional.ofNullable(executable);
    }

    @VisibleForTesting
    static String getOsBinaryWrapperExtension() {
        return SystemInfo.isWindows ? ".cmd" : "";
    }

    static Optional<String> getOsBsPrefix() {
        if (SystemInfo.isWindows) {
            return Optional.of("win32");
        }
        if (SystemInfo.isLinux) {
            return Optional.of("linux");
        }
        if (SystemInfo.isMac) {
            return Optional.of("darwin");
        }
        return Optional.empty();
    }
}

