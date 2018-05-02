package com.reason.ide.facet;

import org.jetbrains.annotations.NotNull;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;

class OCamlFacet extends Facet<OCamlFacetSettings> {

    static final FacetTypeId<OCamlFacet> ID = new FacetTypeId<>("ocaml");

    OCamlFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull OCamlFacetSettings configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @NotNull
    static FacetType<OCamlFacet, OCamlFacetSettings> getFacetType() {
        return FacetTypeRegistry.getInstance().findFacetType(ID);
    }
}
