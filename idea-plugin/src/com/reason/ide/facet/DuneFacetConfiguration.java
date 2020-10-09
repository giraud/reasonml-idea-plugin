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
    name = "DuneFacetConfiguration",
    storages = {@Storage("ocaml-dune.xml")})
public class DuneFacetConfiguration
    implements FacetConfiguration, PersistentStateComponent<DuneFacetConfiguration> {

  public boolean inheritProjectSdk = true;
  public @Nullable String sdkName = null;

  @NotNull
  @Override
  public FacetEditorTab @NotNull [] createEditorTabs(
      @NotNull FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
    return new FacetEditorTab[] {new DuneFacetEditor(editorContext, this)};
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
