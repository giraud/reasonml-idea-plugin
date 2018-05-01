package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OCamlFacetType extends FacetType<OCamlFacet, OCamlFacetConfiguration> {

    public OCamlFacetType() {
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

    @Override
    public Icon getIcon() {
        return Icons.OCL_FILE;
    }
}
