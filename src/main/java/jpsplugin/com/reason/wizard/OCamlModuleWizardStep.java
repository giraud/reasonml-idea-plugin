package jpsplugin.com.reason.wizard;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

class OCamlModuleWizardStep extends ModuleWizardStep {

    private JPanel c_rootPanel;
    private @Nullable JdkComboBox c_sdk;

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
        c_sdk = new JdkComboBox(project, model, null, null, null, null);
    }

    @Override
    public void updateStep() {
    }

    public void updateDataModel() {
    }

    public boolean validate() {
        return true;
    }
}
