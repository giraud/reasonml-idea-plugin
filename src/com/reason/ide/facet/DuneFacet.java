package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class DuneFacet extends Facet<DuneFacetConfiguration> {

    static final String ID_NAME = "ocaml-dune";
    public static final FacetTypeId<DuneFacet> ID = new FacetTypeId<>(ID_NAME);

    DuneFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull DuneFacetConfiguration configuration, Facet underlyingFacet) {
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
}
