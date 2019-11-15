package com.reason.ide.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

@State(name = "DuneFacetConfiguration", storages = {@Storage("ocaml-dune.xml")})
public class DuneFacetConfiguration implements FacetConfiguration, PersistentStateComponent<DuneFacetConfiguration> {

    @SuppressWarnings("WeakerAccess")
    public boolean isEsy = false;

    @NotNull
    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        if (editorContext.isNewFacet()) {
            // Initialize esy
            VirtualFile moduleFile = editorContext.getModule().getModuleFile();
            VirtualFile moduleHome = moduleFile == null ? null : moduleFile.getParent();
            VirtualFile packageJson = moduleHome == null ? null : moduleHome.findChild("package.json");
            isEsy = packageJson != null;
        }
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
