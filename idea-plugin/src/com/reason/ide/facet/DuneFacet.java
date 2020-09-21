package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.reason.sdk.OCamlSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DuneFacet extends Facet<DuneFacetConfiguration> {

  static final String ID_NAME = "ocaml-dune";
  public static final FacetTypeId<DuneFacet> ID = new FacetTypeId<>(ID_NAME);

  DuneFacet(
      @NotNull FacetType facetType,
      @NotNull Module module,
      @NotNull String name,
      @NotNull DuneFacetConfiguration configuration,
      Facet underlyingFacet) {
    super(facetType, module, name, configuration, underlyingFacet);
  }

  @NotNull
  static FacetType<DuneFacet, DuneFacetConfiguration> getFacetType() {
    return FacetTypeRegistry.getInstance().findFacetType(ID);
  }

  @NotNull
  @Override
  public String toString() {
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
