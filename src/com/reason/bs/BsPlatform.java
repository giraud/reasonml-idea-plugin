package com.reason.bs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.ide.ORProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.reason.bs.BsConstants.*;

public class BsPlatform {

    private static final Log LOG = Log.create("bs-platform");

    private BsPlatform() {}

    public static Optional<VirtualFile> findFirstBsPlatformDirectory(@NotNull Project project) {
        return ORProjectManager.findFirstBsContentRoot(project)
                .flatMap(BsPlatform::findBsPlatformPathForConfigFile);
    }

    public static Optional<VirtualFile> findBsPlatformDirectory(@NotNull Project project,
            @NotNull VirtualFile sourceFile) {
        return findBsConfigForFile(project, sourceFile)
                .flatMap(BsPlatform::findBsPlatformPathForConfigFile);
    }

    public static Optional<VirtualFile> findBsbExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsPlatformDirectory(project, sourceFile)
                .flatMap((bsPlatformDirectory) -> findBinaryInBsPlatform(BSB_EXECUTABLE_NAME, bsPlatformDirectory));
    }

    public static Optional<VirtualFile> findBscExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsPlatformDirectory(project, sourceFile)
                .flatMap((bsPlatformDirectory) -> findBinaryInBsPlatform(BSC_EXECUTABLE_NAME, bsPlatformDirectory));
    }

    public static Optional<VirtualFile> findContentRootForFile(@NotNull Project project,
            @NotNull VirtualFile sourceFile) {
        return findBsConfigForFile(project, sourceFile).map(VirtualFile::getParent);
    }

    public static Optional<VirtualFile> findBsConfigForFile(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return ORProjectManager.findAncestorRecursive(project, BS_CONFIG_FILENAME, sourceFile);
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

    private static Optional<VirtualFile> findBsPlatformPathForConfigFile(VirtualFile bsConfigFile) {
        VirtualFile bsPlatform = bsConfigFile.findFileByRelativePath("./node_modules/" + BS_PLATFORM_DIRECTORY_NAME);
        return Optional.ofNullable(bsPlatform)
                .filter(VirtualFile::isDirectory);
    }

    private static Optional<VirtualFile> findBinaryInBsPlatform(String executableName,
            VirtualFile bsPlatformDirectory) {
        Optional<String> platform = getOsBsPrefix();
        if (!platform.isPresent()) {
            LOG.warn("Unable to determine OS prefix.");
            return Optional.empty();
        }
        VirtualFile executable;
        // first, try to find platform-specific binary
        executable = bsPlatformDirectory.findFileByRelativePath("./" + platform + "/" + executableName + ".exe");
        if (executable != null) {
            return Optional.of(executable);
        }
        // next, try to find platform-agnostic wrappers / symlinks
        executable = bsPlatformDirectory.findFileByRelativePath("./" + executableName
                + (SystemInfo.isWindows ? ".cmd" : ""));
        if (executable != null) {
            if (executable.is(VFileProperty.SYMLINK)) {
                return Optional.ofNullable(executable.getCanonicalFile());
            }
            return Optional.of(executable);
        }
        // last, try old locations of binary
        executable = bsPlatformDirectory.findFileByRelativePath("./bin/" + executableName + ".exe");
        if (executable != null) {
            return Optional.of(executable);
        }
        executable = bsPlatformDirectory.findFileByRelativePath("./lib/" + executableName + ".exe");
        return Optional.ofNullable(executable);
    }

    private static Optional<String> getOsBsPrefix() {
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
