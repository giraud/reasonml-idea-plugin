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
import org.jetbrains.annotations.Nullable;

@State(
        name = "OCamlFacetSettings",
        storages = {
                @Storage("ocaml.xml")
        }
)
public class OCamlFacetSettings implements FacetConfiguration, PersistentStateComponent<OCamlFacetSettings> {
    @SuppressWarnings("WeakerAccess")
    public String location = "";

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new OCamlFacetEditor(editorContext, this)};
    }

    @Nullable
    @Override
    public OCamlFacetSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull OCamlFacetSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
