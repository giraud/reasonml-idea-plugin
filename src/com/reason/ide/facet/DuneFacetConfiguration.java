package com.reason.ide.facet;

import com.intellij.facet.*;
import com.intellij.facet.ui.*;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.*;
import org.jetbrains.annotations.*;

@State(name = "DuneFacetConfiguration", storages = {@Storage("ocaml-dune.xml")})
public class DuneFacetConfiguration implements FacetConfiguration, PersistentStateComponent<DuneFacetConfiguration> {
    public boolean inheritProjectSdk = true;
    public @Nullable String sdkName = null;

    @Override
    public @NotNull FacetEditorTab[] createEditorTabs(@NotNull FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new DuneFacetEditor(editorContext, this)};
    }

    @Override
    public DuneFacetConfiguration getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DuneFacetConfiguration state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
