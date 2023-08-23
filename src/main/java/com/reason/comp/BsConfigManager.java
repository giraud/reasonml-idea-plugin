package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.BS_CONFIG_FILENAME;

/**
 * Caches BsConfig available for project.
 */
public class BsConfigManager {
    private final Project myProject;
    private final Map<String, BsConfig> myConfigs = new HashMap<>();

    public BsConfigManager(@NotNull Project project) {
        myProject = project;
    }

    /**
     * Finds the "nearest" `bsconfig.json` to a given file. Searches up the file-system until a
     * `bsconfig.json` is found or the project root is reached.
     *
     * @param sourceFile starting point for search
     * @return `bsconfig.json` file, if found
     */
    public @Nullable VirtualFile findBsConfig(@NotNull VirtualFile sourceFile) {
        return ORFileUtils.findAncestor(myProject, BS_CONFIG_FILENAME, sourceFile);
    }

    public void refresh(@NotNull VirtualFile bsConfigFile) {
        BsConfig bsConfig = BsConfigReader.read(bsConfigFile);
        myConfigs.put(bsConfigFile.getCanonicalPath(), bsConfig);
    }

    public @Nullable BsConfig getConfig(@Nullable VirtualFile bsConfigFile) {
        BsConfig bsConfig = null;

        String canonicalPath = bsConfigFile == null ? null : bsConfigFile.getCanonicalPath();
        if (canonicalPath != null) {
            bsConfig = myConfigs.get(canonicalPath);
            if (bsConfig == null) {
                bsConfig = BsConfigReader.read(bsConfigFile);
                myConfigs.put(canonicalPath, bsConfig);
            }
        }

        return bsConfig;
    }

    public @Nullable BsConfig getNearest(@Nullable VirtualFile sourceFile) {
        VirtualFile bsConfigFile = sourceFile == null ? null : findBsConfig(sourceFile);
        return getConfig(bsConfigFile);
    }
}
