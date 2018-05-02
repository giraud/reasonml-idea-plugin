package com.reason.ide.facet;

import javax.swing.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.reason.icons.Icons;

public class BsFacetType extends FacetType<BsFacet, BsFacetSettings> {

    public BsFacetType() {
        super(BsFacet.ID, "bucklescript", "Bucklescript");
    }

    @Override
    public BsFacetSettings createDefaultConfiguration() {
        return new BsFacetSettings();
    }

    @Override
    public BsFacet createFacet(@NotNull Module module, String name, @NotNull BsFacetSettings configuration, @Nullable Facet underlyingFacet) {
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
