package com.reason.comp.bs;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.esy.*;
import com.reason.ide.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

public class BsPlatform {
    private static final Log LOG = Log.create("platform.bs");

    private BsPlatform() {
    }

    public static @Nullable VirtualFile findBinaryPathForConfigFile(@NotNull Project project, @NotNull VirtualFile configFile) {
        return ORPlatform.findCompilerPathInNodeModules(project, configFile, BS_DIR, BSC_EXE_NAME);
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
        return Optional.ofNullable(ORFileUtils.findAncestor(project, BS_CONFIG_FILENAME, sourceFile));
    }

    public static @NotNull Optional<VirtualFile> findContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile).map(VirtualFile::getParent);
    }

    public static @NotNull Optional<VirtualFile> findBsbExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile)
                .flatMap(bsConfig -> {
                    VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSB_EXE_NAME);
                    return Optional.ofNullable(binDir == null ? null : ORPlatform.findBinary(binDir, BSB_EXE_NAME));
                });
    }

    public static @Nullable VirtualFile findBscExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return findBsConfig(project, sourceFile)
                .flatMap(bsConfig -> {
                    VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSC_EXE_NAME);
                    return Optional.ofNullable(binDir == null ? null : ORPlatform.findBinary(binDir, BSC_EXE_NAME));
                }).orElse(null);
    }

    public static Optional<VirtualFile> findEsyExecutable(@NotNull Project project) {
        String esyExecutable = project.getService(ORSettings.class).getEsyExecutable();
        if (esyExecutable.isEmpty()) {
            return Esy.findEsyExecutable();
        }
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByPath(esyExecutable));
    }

    public static @Nullable VirtualFile findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsPlatformDir = findBsConfig(project, sourceFile)
                .flatMap(bsConfig -> Optional.ofNullable(ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSC_EXE_NAME)))
                .orElse(null);
        if (bsPlatformDir == null) {
            return null;
        }

        VirtualFile binaryInBsPlatform;

        // first, try standard name
        binaryInBsPlatform = ORPlatform.findBinary(bsPlatformDir, REFMT_EXE_NAME);
        if (binaryInBsPlatform == null) {
            // next, try alternative names
            binaryInBsPlatform = ORPlatform.findBinary(bsPlatformDir, "refmt3");
            if (binaryInBsPlatform == null) {
                binaryInBsPlatform = ORPlatform.findBinary(bsPlatformDir, "bsrefmt");
            }
        }

        return binaryInBsPlatform;
    }
}
