package com.reason.ide.repl;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReplRunSettingsEditor extends SettingsEditor<ReplRunConfiguration> {
    private final Project m_project;
    private JPanel c_rootPanel;
    private JdkComboBox c_sdk;

    ReplRunSettingsEditor(Project project) {
        m_project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull ReplRunConfiguration runConfiguration) {
        c_sdk.setSelectedJdk(runConfiguration.getSdk());
    }

    @Override
    protected void applyEditorTo(@NotNull ReplRunConfiguration runConfiguration) /*throws ConfigurationException*/ {
        runConfiguration.setSdk(c_sdk.getSelectedJdk());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return c_rootPanel;
    }

    private void createUIComponents() {
        ProjectSdksModel model = new ProjectSdksModel();
        model.reset(m_project);

        c_sdk = new JdkComboBox(model, sdkTypeId -> "OCaml SDK".equals(sdkTypeId.getName()));
    }
}
