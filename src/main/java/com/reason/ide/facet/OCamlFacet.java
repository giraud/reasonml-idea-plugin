package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

class OCamlFacet extends Facet<OCamlFacetSettings> {

    static final FacetTypeId<OCamlFacet> ID = new FacetTypeId<>("ocaml");

    OCamlFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull OCamlFacetSettings configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    static FacetType<OCamlFacet, OCamlFacetSettings> getFacetType() {
        return FacetTypeRegistry.getInstance().findFacetType(ID);
    }

}
