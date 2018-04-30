package com.reason.ide.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OCamlFacetConfiguration implements FacetConfiguration, PersistentStateComponent<OCamlProperties> {
    private OCamlProperties m_state = new OCamlProperties();

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new OCamlFacetEditor(editorContext, this)};
    }

    @Nullable
    @Override
    public OCamlProperties getState() {
        return m_state;
    }

    @Override
    public void loadState(@NotNull OCamlProperties state) {
        m_state = state;
    }
}
