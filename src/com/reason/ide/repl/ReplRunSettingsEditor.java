package com.reason.ide.repl;

import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.*;
import com.intellij.openapi.util.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

import static com.intellij.openapi.roots.ui.configuration.JdkComboBox.*;

public class ReplRunSettingsEditor extends SettingsEditor<ReplRunConfiguration> {
    private final Project m_project;
    private JPanel c_rootPanel;
    private JdkComboBox c_sdk;

    ReplRunSettingsEditor(Project project) {
        m_project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull ReplRunConfiguration runConfiguration) {
        Sdk odk = runConfiguration.getSdk();
        if (odk != null) {
            for (int i = 0; i < c_sdk.getItemCount(); i++) {
                JdkComboBox.JdkComboBoxItem comboSdk = c_sdk.getItemAt(i);
                String name = comboSdk.getSdkName();
                if (name != null && name.equals(odk.getName())) {
                    c_sdk.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void applyEditorTo(@NotNull ReplRunConfiguration runConfiguration) {
        runConfiguration.setSdk(c_sdk.getSelectedJdk());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return c_rootPanel;
    }

    private void createUIComponents() {
        ProjectSdksModel model = new ProjectSdksModel();
        Condition<SdkTypeId> filter = sdkTypeId -> OCamlSdkType.ID.equals(sdkTypeId.getName());

        model.reset(m_project);
        c_sdk = new JdkComboBox(m_project, model, filter, getSdkFilter(filter), filter, null);
    }
}
