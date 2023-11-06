package com.reason.comp.bs;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.*;
import com.reason.comp.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

public class BsPlatform {
    private static final Log LOG = Log.create("bs.platform");

    private BsPlatform() {
    }

    public static @NotNull List<VirtualFile> findConfigFiles(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        List<VirtualFile> validConfigs = FilenameIndex.getVirtualFilesByName(BS_CONFIG_FILENAME, scope).stream()
                .filter(bsConfigFile -> {
                    VirtualFile bsbBin = ORPlatform.findCompilerPathInNodeModules(project, bsConfigFile, BS_DIR, BSC_EXE_NAME);
                    VirtualFile resBin = ORPlatform.findCompilerPathInNodeModules(project, bsConfigFile, RESCRIPT_DIR, BSC_EXE_NAME);
                    return bsbBin != null && resBin == null;
                })
                .sorted(ORFileUtils.FILE_DEPTH_COMPARATOR)
                .toList();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Valid configs for project=\"" + project.getName() + "\": [" + Joiner.join(",", validConfigs) + "]");
        }

        return validConfigs;
    }

    public static @Nullable VirtualFile findConfigFile(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        return sourceFile != null
                ? ORFileUtils.findAncestor(project, sourceFile, BS_CONFIG_FILENAME)
                : findConfigFiles(project).stream().findFirst().orElse(null);
    }

    public static @Nullable VirtualFile findBsbExecutable(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        VirtualFile configFile = findConfigFile(project, sourceFile);
        VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, configFile, BS_DIR, BSB_EXE_NAME);
        return binDir != null ? ORPlatform.findBinary(binDir, BSB_EXE_NAME) : null;
    }

    public static @Nullable VirtualFile findBscExecutable(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        VirtualFile configFile = findConfigFile(project, sourceFile);
        VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, configFile, BS_DIR, BSC_EXE_NAME);
        return binDir != null ? ORPlatform.findBinary(binDir, BSC_EXE_NAME) : null;
    }

    public static @Nullable VirtualFile findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile bsConfigFile = ORFileUtils.findAncestor(project, sourceFile, BS_CONFIG_FILENAME);
        VirtualFile bsDir = ORPlatform.findCompilerPathInNodeModules(project, bsConfigFile, BS_DIR, BSC_EXE_NAME);
        if (bsDir == null) {
            return null;
        }

        VirtualFile binaryInBsPlatform;

        // first, try standard name
        binaryInBsPlatform = ORPlatform.findBinary(bsDir, REFMT_EXE_NAME);
        if (binaryInBsPlatform == null) {
            // next, try alternative names
            binaryInBsPlatform = ORPlatform.findBinary(bsDir, "refmt3");
            if (binaryInBsPlatform == null) {
                binaryInBsPlatform = ORPlatform.findBinary(bsDir, "bsrefmt");
            }
        }

        return binaryInBsPlatform;
    }
}
