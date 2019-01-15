package com.reason;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class OCamlModuleWizardStep extends ModuleWizardStep {

    @Override
    public JComponent getComponent() {
        return new JLabel("Provide some settings here");
    }

    @Override
    public void updateDataModel() {
    }
}
