package com.reason.ide.facet;

import org.jetbrains.annotations.NotNull;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;

public class BsFacet extends Facet<BsFacetSettings> {

    static final FacetTypeId<BsFacet> ID = new FacetTypeId<>("bucklescript");

    BsFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull BsFacetSettings configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @NotNull
    static FacetType<BsFacet, BsFacetSettings> getFacetType() {
        return FacetTypeRegistry.getInstance().findFacetType(ID);
    }

    @Override
    public String toString() {
        return getModule().getName();
    }
}
