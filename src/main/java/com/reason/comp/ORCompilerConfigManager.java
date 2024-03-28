package com.reason.comp;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.comp.bs.*;
import com.reason.comp.rescript.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

/**
 * Caches BsConfig available for project.
 */
@Service(Service.Level.PROJECT)
public final class ORCompilerConfigManager {
    private final Project myProject;
    private final Map<String, BsConfig> myConfigs = new HashMap<>();

    public ORCompilerConfigManager(@NotNull Project project) {
        myProject = project;
    }

    public void refresh(@NotNull VirtualFile configFile) {
        BsConfig config = FileHelper.isBsConfigJson(configFile) ? BsConfigReader.read(configFile) : ResConfigReader.read(configFile);
        myConfigs.put(configFile.getCanonicalPath(), config);
    }

    public @Nullable BsConfig getConfig(@Nullable VirtualFile configFile) {
        BsConfig config = null;

        String canonicalPath = configFile == null ? null : configFile.getCanonicalPath();
        if (canonicalPath != null) {
            config = myConfigs.get(canonicalPath);
            if (config == null) {
                config = FileHelper.isBsConfigJson(configFile) ? BsConfigReader.read(configFile) : ResConfigReader.read(configFile);
                myConfigs.put(canonicalPath, config);
            }
        }

        return config;
    }

    /**
     * Finds the "nearest" bsconfig.json/rescript.json to a given file.
     * Searches up the file-system until one file is found or the project root is reached.
     *
     * @param sourceFile starting point for search
     * @return configuration file, if found
     */
    public @Nullable VirtualFile findNearestConfigFile(@NotNull VirtualFile sourceFile) {
        return ORFileUtils.findOneOfAncestor(myProject, sourceFile, RESCRIPT_CONFIG_FILENAME, BS_CONFIG_FILENAME);
    }

    public @Nullable BsConfig getNearestConfig(@Nullable VirtualFile sourceFile) {
        VirtualFile configFile = sourceFile == null ? null : findNearestConfigFile(sourceFile);
        return getConfig(configFile);
    }

    public @Nullable BsConfig getNearestConfig(@Nullable PsiFile psiFile) {
        VirtualFile virtualFile = psiFile == null ? null : ORFileUtils.getVirtualFile(psiFile);
        return getNearestConfig(virtualFile);
    }
}
