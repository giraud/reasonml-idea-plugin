package com.reason.ide.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "BsFacetConfiguration", storages = {@Storage("bucklescript.xml")})
public class BsFacetConfiguration implements FacetConfiguration, PersistentStateComponent<BsFacetConfiguration> {
    @SuppressWarnings("WeakerAccess")
    public String location = "";
    @SuppressWarnings("WeakerAccess")
    public boolean refmtOnSave = true;
    @SuppressWarnings("WeakerAccess")
    public String refmtWidth = "120";

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new BsFacetEditor(editorContext, this)};
    }

    @Override
    public BsFacetConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BsFacetConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
