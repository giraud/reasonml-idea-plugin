package com.reason.comp.dune;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class DunePlatform {
    public static final String DUNE_EXECUTABLE_NAME = "dune";
    public static final String DUNE_PROJECT_FILENAME = "dune-project";
    public static final String DUNE_FILENAME = "dune";
    public static final String LEGACY_JBUILDER_FILENAME = "jbuild";

    private DunePlatform() {
    }

    public static @NotNull List<VirtualFile> findConfigFiles(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

        return FilenameIndex.getVirtualFilesByName(DUNE_PROJECT_FILENAME, scope).stream()
                .sorted(ORFileUtils.FILE_DEPTH_COMPARATOR)
                .toList();
    }

    public static @Nullable VirtualFile findContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        Module module = Platform.getModule(project, sourceFile);
        ModuleRootManager rootManager = module != null ? ModuleRootManager.getInstance(module) : null;
        VirtualFile[] contentRoots = rootManager != null ? rootManager.getContentRoots() : null;
        return contentRoots != null ? contentRoots[0] : null;
    }
}
