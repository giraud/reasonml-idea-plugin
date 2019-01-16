package com.reason.wizard;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.Messages;
import com.reason.OCamlSdk;

import javax.swing.*;

public class OCamlModuleWizardStep extends ModuleWizardStep {

    private JPanel c_rootPanel;
    private JdkComboBox c_sdk;

    private final WizardContext m_context;

    public OCamlModuleWizardStep(WizardContext context) {
        m_context = context;
    }

    public JComponent getComponent() {
        return c_rootPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return c_sdk;
    }

    private void createUIComponents() {
        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(ProjectManager.getInstance().getDefaultProject());
        c_sdk = new JdkComboBox(model, sdkTypeId -> OCamlSdk.ID.equals(sdkTypeId.getName()));
    }

    @Override
    public void updateStep() {
        Sdk odk = m_context.getProjectJdk();
        if (odk == null) {
//            JavaSdkVersion requiredJdkVersion = myProjectDescriptor != null ? myProjectDescriptor.getRequiredJdkVersion() : null;
//            if (requiredJdkVersion != null) {
//                myProjectJdksConfigurable.selectJdkVersion(requiredJdkVersion);
//            }
        }
    }

    public void updateDataModel() {
        m_context.setProjectJdk(c_sdk.getSelectedJdk());
    }

    public boolean validate() {
        Sdk odk = c_sdk.getSelectedJdk();
        if (odk == null && !ApplicationManager.getApplication().isUnitTestMode()) {
            int result = Messages.showOkCancelDialog("Do you want to create a project with no SDK assigned?\\nAn SDK is required for compiling as well as for the standard SDK modules resolution and type inference.",
                    IdeBundle.message("title.no.jdk.specified"), Messages.getWarningIcon());
            return result == Messages.OK;
        }
        return true;
    }
}
