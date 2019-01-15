package com.reason;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.SdkSettingsStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OCamlModuleWizardStep extends SdkSettingsStep {
    public OCamlModuleWizardStep(SettingsStep settingsStep, @NotNull ModuleBuilder moduleBuilder, @NotNull Condition<SdkTypeId> sdkFilter) {
        super(settingsStep, moduleBuilder, sdkFilter);
    }

    @Override
    public JComponent getComponent() {
        return new JLabel("Provide some settings here");
    }

    @Override
    public void updateDataModel() {
    }
}
