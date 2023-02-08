package com.reason.ide.repl;

import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ReplRunSettingsEditor extends SettingsEditor<ReplRunConfiguration> {
    private final Project myProject;
    private JPanel myRootPanel;
    private JLabel mySwitchName;

    ReplRunSettingsEditor(@NotNull Project project) {
        myProject = project;
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return myRootPanel;
    }

    private void createUIComponents() {
        init();
    }

    @Override
    protected void resetEditorFrom(@NotNull ReplRunConfiguration configuration) {
        init();
    }

    @Override
    protected void applyEditorTo(@NotNull ReplRunConfiguration runConfiguration) {
    }

    private void init() {
        ORSettings settings = myProject.getService(ORSettings.class);
        mySwitchName.setText(settings.getSwitchName());
    }
}
