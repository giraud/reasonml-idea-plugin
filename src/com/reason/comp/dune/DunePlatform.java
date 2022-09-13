package com.reason.comp.dune;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class DunePlatform {
    public static final String DUNE_EXECUTABLE_NAME = "dune";
    public static final String DUNE_PROJECT_FILENAME = "dune-project";
    public static final String DUNE_FILENAME = "dune";
    public static final String LEGACY_JBUILDER_FILENAME = "jbuild";

    private DunePlatform() {
    }

    public static @Nullable VirtualFile findContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        Module module = Platform.getModule(project, sourceFile);
        ModuleRootManager rootManager = module == null ? null : ModuleRootManager.getInstance(module);
        VirtualFile[] contentRoots = rootManager == null ? null : rootManager.getContentRoots();
        return contentRoots == null ? null : contentRoots[0];
    }
}
