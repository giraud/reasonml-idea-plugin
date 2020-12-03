package com.reason.dune;

import com.intellij.facet.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.ide.facet.*;
import org.jetbrains.annotations.*;

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
