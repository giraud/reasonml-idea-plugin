package com.reason.comp.bs;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.comp.esy.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

public class BsPlatform {
    private BsPlatform() {
    }

    public static @Nullable VirtualFile findBinaryPathForConfigFile(@NotNull Project project, @NotNull VirtualFile configFile) {
        return ORPlatform.findCompilerPathInNodeModules(project, configFile, BS_DIR, BSC_EXE_NAME);
    }

    public static @Nullable VirtualFile findContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = project.getService(BsConfigManager.class).findBsConfig(sourceFile);
        return bsConfig == null ? null : bsConfig.getParent();
    }

    public static @Nullable VirtualFile findBsbExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = project.getService(BsConfigManager.class).findBsConfig(sourceFile);
        VirtualFile binDir = bsConfig == null ? null : ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSB_EXE_NAME);
        return binDir == null ? null : ORPlatform.findBinary(binDir, BSB_EXE_NAME);
    }

    public static @Nullable VirtualFile findBscExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = project.getService(BsConfigManager.class).findBsConfig(sourceFile);
        VirtualFile binDir = bsConfig == null ? null : ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSC_EXE_NAME);
        return binDir == null ? null : ORPlatform.findBinary(binDir, BSC_EXE_NAME);
    }

    public static Optional<VirtualFile> findEsyExecutable(@NotNull Project project) {
        String esyExecutable = project.getService(ORSettings.class).getEsyExecutable();
        if (esyExecutable.isEmpty()) {
            return Esy.findEsyExecutable();
        }
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByPath(esyExecutable));
    }

    public static @Nullable VirtualFile findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = project.getService(BsConfigManager.class).findBsConfig(sourceFile);
        VirtualFile bsPlatformDir = bsConfig == null ? null : ORPlatform.findCompilerPathInNodeModules(project, bsConfig, BS_DIR, BSC_EXE_NAME);
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

    public static @Nullable BsConfig readConfig(@NotNull VirtualFile contentRoot) {
        // Read bsConfig to get the compilation directives
        VirtualFile bsConfigFile = contentRoot.findChild(BS_CONFIG_FILENAME);
        return bsConfigFile == null ? null : BsConfigReader.read(bsConfigFile);
    }
}
