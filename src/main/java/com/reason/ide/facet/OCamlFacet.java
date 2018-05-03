package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

class OCamlFacet extends Facet<OCamlFacetConfiguration> {

    static final FacetTypeId<OCamlFacet> ID = new FacetTypeId<>("ocaml");

    OCamlFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull OCamlFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @NotNull
    static FacetType<OCamlFacet, OCamlFacetConfiguration> getFacetType() {
        return FacetTypeRegistry.getInstance().findFacetType(ID);
    }
}
