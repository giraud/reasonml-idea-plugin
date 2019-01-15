package com.reason.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.reason.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BsFacetType extends FacetType<BsFacet, BsFacetConfiguration> {

    public BsFacetType() {
        super(BsFacet.ID, "bucklescript", "Bucklescript");
    }

    @NotNull
    @Override
    public BsFacetConfiguration createDefaultConfiguration() {
        return new BsFacetConfiguration();
    }

    @Nullable
    @Override
    public BsFacet createFacet(@NotNull Module module, @NotNull String name, @NotNull BsFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new BsFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Icons.BUCKLESCRIPT;
    }
}
