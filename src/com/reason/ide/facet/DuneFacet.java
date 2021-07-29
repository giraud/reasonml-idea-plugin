package com.reason.ide.facet;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

public class DuneFacet extends Facet<DuneFacetConfiguration> {

  static final String ID_NAME = "ocaml-dune";
  public static final FacetTypeId<DuneFacet> ID = new FacetTypeId<>(ID_NAME);

  DuneFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull DuneFacetConfiguration configuration, Facet underlyingFacet) {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  static @NotNull FacetType<DuneFacet, DuneFacetConfiguration> getFacetType() {
    return FacetTypeRegistry.getInstance().findFacetType(ID);
  }

  @Override
  public @NotNull String toString() {
    return getModule().getName();
  }

  @Nullable
  public Sdk getODK() {
    DuneFacetConfiguration configuration = getConfiguration();
    if (configuration.inheritProjectSdk) {
      return OCamlSdkType.getSDK(getModule().getProject());
    }

    if (configuration.sdkName != null) {
      return ProjectJdkTable.getInstance().findJdk(configuration.sdkName);
    }

    return null;
  }
}
