package com.reason.ide.facet;

import com.intellij.facet.*;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BsFacet extends Facet<BsFacetConfiguration> {

    static final FacetTypeId<BsFacet> ID = new FacetTypeId<>("bucklescript");

    BsFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull BsFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @Nullable
    public static BsFacetConfiguration getConfiguration(@NotNull Module module) {
        BsFacet facet = FacetManager.getInstance(module).getFacetByType(BsFacet.ID);
        if (facet != null) {
            return facet.getConfiguration();
        }
        return null;
    }

    @NotNull
    static FacetType<BsFacet, BsFacetConfiguration> getFacetType() {
        return FacetTypeRegistry.getInstance().findFacetType(ID);
    }

    @Override
    public String toString() {
        return getModule().getName();
    }
}
