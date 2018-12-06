package com.reason.ide.facet;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "BsFacetConfiguration", storages = {@Storage("bucklescript.xml")})
public class BsFacetConfiguration implements FacetConfiguration, PersistentStateComponent<BsFacetConfiguration> {
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public String location = "";
    @SuppressWarnings("WeakerAccess")
    public boolean refmtOnSave = true;
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public String refmtWidth = "";

    @NotNull
    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[]{new BsFacetEditor(editorContext, this)};
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        // TODO implement method
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        // TODO implement method
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
