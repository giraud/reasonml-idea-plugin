package com.reason.comp.dune;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import jpsplugin.com.reason.Platform;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Dune {

  public static final String DUNE_EXECUTABLE_NAME = "dune";
  public static final String DUNE_PROJECT_FILENAME = "dune-project";
  public static final String DUNE_FILENAME = "dune";
  public static final String LEGACY_JBUILDER_FILENAME = "jbuild";

  private Dune() {
  }

  static @Nullable DuneFacet getFacet(@NotNull Project project, @Nullable VirtualFile source) {
    Module module = Platform.getModule(project, source);
    return module == null ? null : FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
  }

}
