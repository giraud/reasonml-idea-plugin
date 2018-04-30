package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OcamlFacetType extends FacetType<OCamlFacet, OCamlFacetConfiguration> {

    public OcamlFacetType() {
        super(OCamlFacet.TYPE_ID, "ocaml", "OCaml");
    }

    @Override
    public OCamlFacetConfiguration createDefaultConfiguration() {
        return new OCamlFacetConfiguration();
    }

    @Override
    public OCamlFacet createFacet(@NotNull Module module, String name, @NotNull OCamlFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new OCamlFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return true;
    }

}
