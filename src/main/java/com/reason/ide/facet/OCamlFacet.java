package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OCamlFacet extends Facet<OCamlFacetConfiguration> {

    public static final FacetTypeId<OCamlFacet> TYPE_ID = new FacetTypeId<>("ocaml");

    public OCamlFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull OCamlFacetConfiguration configuration, Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @Nullable
    public static OCamlFacet getFacet(@NotNull Module module) {
        return FacetManager.getInstance(module).getFacetByType(TYPE_ID);
    }

}
