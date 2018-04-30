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

    //should only be called from write action
    //public static void createFacet(@NotNull Module module) {
    //    FacetManager facetManager = FacetManager.getInstance(module);
    //    ErlangFacetType ft = FacetType.findInstance(ErlangFacetType.class);
    //    ErlangFacet prev = facetManager.getFacetByType(ft.getId());
    //    if (prev != null) return;
    //    ErlangFacet facet = facetManager.createFacet(ft, ErlangFacetConstants.NAME, null);
    //    ModifiableFacetModel facetModel = facetManager.createModifiableModel();
    //    facetModel.addFacet(facet);
    //    facetModel.commit();
    //}
}
