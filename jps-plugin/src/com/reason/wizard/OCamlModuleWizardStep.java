package com.reason.wizard;

import static com.intellij.openapi.roots.ui.configuration.JdkComboBox.getSdkFilter;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Condition;
import com.reason.sdk.OCamlSdkType;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

class OCamlModuleWizardStep extends ModuleWizardStep {

  private JPanel c_rootPanel;
  private @Nullable JdkComboBox c_sdk;

  private final WizardContext m_context;

  public OCamlModuleWizardStep(WizardContext context) {
    m_context = context;
  }

  public JComponent getComponent() {
    return c_rootPanel;
  }

  public @Nullable JComponent getPreferredFocusedComponent() {
    return c_sdk;
  }

  private void createUIComponents() {
    ProjectSdksModel model = new ProjectSdksModel();
    Project project = ProjectManager.getInstance().getDefaultProject();

    model.reset(project);
    Condition<SdkTypeId> filter = sdkTypeId -> OCamlSdkType.ID.equals(sdkTypeId.getName());
    c_sdk = new JdkComboBox(project, model, filter, getSdkFilter(filter), filter, null);
  }

  @Override
  public void updateStep() {
    // Sdk odk = m_context.getProjectJdk();
    // if (odk == null) {
    //            JavaSdkVersion requiredJdkVersion = myProjectDescriptor != null ?
    // myProjectDescriptor.getRequiredJdkVersion() : null;
    //            if (requiredJdkVersion != null) {
    //                myProjectJdksConfigurable.selectJdkVersion(requiredJdkVersion);
    //            }
    // }
  }

  public void updateDataModel() {
    if (c_sdk != null) {
      m_context.setProjectJdk(c_sdk.getSelectedJdk());
    }
  }

  public boolean validate() {
    Sdk odk = c_sdk == null ? null : c_sdk.getSelectedJdk();
    if (odk == null && !ApplicationManager.getApplication().isUnitTestMode()) {
      int result =
          Messages.showOkCancelDialog(
              "Do you want to create a project with no SDK assigned?\\nAn SDK is required for compiling as well as for the standard SDK modules resolution and type inference.",
              IdeBundle.message("title.no.jdk.specified"),
              Messages.getOkButton(),
              Messages.getCancelButton(),
              Messages.getWarningIcon());
      return result == Messages.OK;
    }
    return true;
  }
}
