package com.reason.comp.dune;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.facet.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class DunePlatform {
    public static final String DUNE_EXECUTABLE_NAME = "dune";
    public static final String DUNE_PROJECT_FILENAME = "dune-project";
    public static final String DUNE_FILENAME = "dune";
    public static final String LEGACY_JBUILDER_FILENAME = "jbuild";

    private DunePlatform() {
    }

    public static @Nullable DuneFacet getFacet(@NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            DuneFacet duneFacet = FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
            if (duneFacet != null) {
                return duneFacet;
            }
        }
        return null;
    }

    static @Nullable DuneFacet getFacet(@NotNull Project project, @Nullable VirtualFile source) {
        Module module = Platform.getModule(project, source);
        if (module == null) {
            return getFacet(project);
        }

        return FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
    }

}
