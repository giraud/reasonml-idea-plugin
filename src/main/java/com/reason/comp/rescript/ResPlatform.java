package com.reason.comp.rescript;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

public class ResPlatform {
    private static final Log LOG = Log.create("rescript.platform");

    private ResPlatform() {
    }

    public static @NotNull List<VirtualFile> findConfigFiles(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        Collection<VirtualFile> configs = FilenameIndex.getVirtualFilesByName(RESCRIPT_CONFIG_FILENAME, scope);
        configs.addAll(FilenameIndex.getVirtualFilesByName(BS_CONFIG_FILENAME, scope));

        List<VirtualFile> validConfigs = configs.stream()
                .filter(configFile -> ORPlatform.findCompilerPathInNodeModules(project, configFile, RESCRIPT_DIR, BSC_EXE_NAME) != null)
                .sorted(ORFileUtils.FILE_DEPTH_COMPARATOR)
                .toList();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Valid configs for project=\"" + project.getName() + "\": [" + Joiner.join(",", validConfigs) + "]");
        }

        return validConfigs;
    }

    static @Nullable VirtualFile findConfigFile(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        return sourceFile != null
                ? ORFileUtils.findOneOfAncestor(project, sourceFile, RESCRIPT_CONFIG_FILENAME, BS_CONFIG_FILENAME)
                : findConfigFiles(project).stream().findFirst().orElse(null);
    }

    public static @Nullable VirtualFile findBscExecutable(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        VirtualFile configFile = findConfigFile(project, sourceFile);
        VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, configFile, RESCRIPT_DIR, BSC_EXE_NAME);
        return binDir != null ? ORPlatform.findBinary(binDir, BSC_EXE_NAME) : null;
    }

    public static @Nullable VirtualFile findRescriptExecutable(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        VirtualFile configFile = findConfigFile(project, sourceFile);
        VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, configFile, RESCRIPT_DIR, RESCRIPT_EXE_NAME);
        return binDir != null ? ORPlatform.findBinary(binDir, RESCRIPT_EXE_NAME) : null;
    }

    public static @Nullable VirtualFile findRefmtExecutable(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile configFile = ORFileUtils.findOneOfAncestor(project, sourceFile, RESCRIPT_CONFIG_FILENAME, BS_CONFIG_FILENAME);
        VirtualFile binDir = ORPlatform.findCompilerPathInNodeModules(project, configFile, RESCRIPT_DIR, BSC_EXE_NAME);
        return binDir != null ? ORPlatform.findBinary(binDir, REFMT_EXE_NAME) : null;
    }

    public static boolean isDevSource(@NotNull VirtualFile sourceFile, @NotNull VirtualFile contentRoot, @NotNull BsConfig config) {
        for (String devSource : config.getDevSources()) {
            VirtualFile devFile = contentRoot.findFileByRelativePath(devSource);
            if (devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
                return true;
            }
        }
        return false;
    }
}
