package com.reason.comp.rescript;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import static com.reason.comp.ORConstants.*;

public class ResPlatform {
    static @Nullable VirtualFile findConfigFile(Project project) {
        VirtualFile rootDir = ORProjectManager.findFirstBsContentRoot(project).orElse(null);
        return rootDir == null ? null : rootDir.findFileByRelativePath(BS_CONFIG_FILENAME);
    }

    public static @Nullable VirtualFile findBinaryPathForConfigFile(@NotNull Project project, @NotNull VirtualFile configFile) {
        return ORPlatform.findCompilerPathInNodeModules(project, configFile, RESCRIPT_EXE_NAME, BSC_EXE_NAME);
    }

    public static @Nullable VirtualFile findBscExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = ORFileUtils.findAncestor(project, BS_CONFIG_FILENAME, sourceFile);
        if (bsConfig != null) {
            VirtualFile binDir = findBinaryPathForConfigFile(project, bsConfig);
            return binDir == null ? null : ORPlatform.findBinary(binDir, BSC_EXE_NAME);
        }
        return null;
    }

    public static @Nullable VirtualFile findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfig = ORFileUtils.findAncestor(project, BS_CONFIG_FILENAME, sourceFile);
        VirtualFile binDir = bsConfig == null ? null : findBinaryPathForConfigFile(project, bsConfig);
        return binDir == null ? null : ORPlatform.findBinary(binDir, REFMT_EXE_NAME);
    }
}
