package com.reason.ide.facet;

import org.jetbrains.annotations.NotNull;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "OCamlFacetSettings", storages = {@Storage("bucklescript.xml")})
public class BsFacetSettings implements FacetConfiguration, PersistentStateComponent<BsFacetSettings> {
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
    public BsFacetSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BsFacetSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
