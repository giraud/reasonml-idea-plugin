package com.reason.ide.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.util.Condition;
import com.reason.ide.console.ORToolWindowManager;
import com.reason.sdk.OCamlSdkType;
import java.awt.event.ItemEvent;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DuneFacetEditor extends FacetEditorTab {

  private final @NotNull DuneFacetConfiguration m_configuration;
  private final @NotNull FacetEditorContext m_editorContext;

  private JPanel f_root;
  private JCheckBox f_inheritProjectSDKCheck;
  private @Nullable JdkComboBox f_sdkSelect;

  DuneFacetEditor(
      @NotNull FacetEditorContext editorContext, @NotNull DuneFacetConfiguration configuration) {
    m_editorContext = editorContext;
    m_configuration = configuration;
  }

  @NotNull
  @Nls
  @Override
  public String getDisplayName() {
    return "Dune";
  }

  private void createUIComponents() {
    Module m_module = m_editorContext.getModule();
    ProjectSdksModel model = new ProjectSdksModel();
    Project project = m_module.getProject();
    Condition<SdkTypeId> filter = sdkTypeId -> OCamlSdkType.ID.equals(sdkTypeId.getName());

    model.reset(project);
    f_sdkSelect = new JdkComboBox(model, filter);
  }

  @NotNull
  @Override
  public JComponent createComponent() {
    DuneFacetConfiguration state = m_configuration.getState();
    assert state != null;

    if (f_sdkSelect != null) {
      f_inheritProjectSDKCheck.addItemListener(
          itemEvent -> f_sdkSelect.setEnabled(itemEvent.getStateChange() == ItemEvent.DESELECTED));
      f_inheritProjectSDKCheck.setSelected(state.inheritProjectSdk);

      if (state.inheritProjectSdk) {
        Module module = m_editorContext.getModule();
        Sdk odk = OCamlSdkType.getSDK(module.getProject());
        if (odk != null) {
          f_sdkSelect.setSelectedJdk(odk);
        }
      } else {
        int itemCount = f_sdkSelect.getItemCount();
        for (int i = 0; i < itemCount; i++) {
          String odkName = f_sdkSelect.getItemAt(i).getSdkName();
          if (odkName != null && odkName.equals(m_configuration.sdkName)) {
            f_sdkSelect.setSelectedIndex(i);
            break;
          }
        }
      }
    }

    return f_root;
  }

  @Override
  public boolean isModified() {
    Sdk odk = f_sdkSelect == null ? null : f_sdkSelect.getSelectedJdk();
    String odkName = odk == null ? "" : odk.getName();
    String confOdkName = m_configuration.sdkName == null ? "" : m_configuration.sdkName;
    return m_configuration.inheritProjectSdk != f_inheritProjectSDKCheck.isSelected()
        || !odkName.equals(confOdkName);
  }

  @Override
  public void apply() throws ConfigurationException {
    super.apply();
    m_configuration.inheritProjectSdk = f_inheritProjectSDKCheck.isSelected();
    Sdk odk = f_sdkSelect == null ? null : f_sdkSelect.getSelectedJdk();
    m_configuration.sdkName = odk == null ? null : odk.getName();

    // @TODO see https://github.com/reasonml-editor/reasonml-idea-plugin/issues/243
    // show tool window if dune is now configured
    // should use a listener instead as this doesn't trigger when the facet is removed
    ORToolWindowManager toolWindowManager =
        ORToolWindowManager.getInstance(m_editorContext.getProject());
    ApplicationManager.getApplication().invokeLater(toolWindowManager::showHideToolWindows);
  }
}
