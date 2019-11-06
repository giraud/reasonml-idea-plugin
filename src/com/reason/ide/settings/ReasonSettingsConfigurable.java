package com.reason.ide.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private final ReasonSettings m_settings;

    private JPanel f_rootPanel;
    private TextFieldWithBrowseButton f_bsLocation;
    private JTextField f_columnWidth;
    private JCheckBox f_reformatOnSave;
    private TextFieldWithBrowseButton f_workingDir;
    private JCheckBox f_enabled;

    public ReasonSettingsConfigurable(ReasonSettings settings) {
        m_settings = settings;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @NotNull
    @Nls
    @Override
    public String getDisplayName() {
        return "Reason";
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "settings.reason";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        f_bsLocation.addBrowseFolderListener("Choose bs-platform directory: ", null, m_settings.getProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        f_workingDir.addBrowseFolderListener("Choose a working directory: ", null, m_settings.getProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        return f_rootPanel;
    }

    @Override
    public void apply() {
        m_settings.setEnabled(f_enabled.isSelected());
        m_settings.setLocation(f_bsLocation.getText().trim());
        m_settings.setWorkingDir(f_workingDir.getText().trim());
        m_settings.setRefmtOnSave(f_reformatOnSave.isSelected());
        m_settings.setRefmtWidth(f_columnWidth.getText().trim());
    }

    @Override
    public boolean isModified() {
        boolean sameEnabled = f_enabled.isSelected() == m_settings.isEnabled();
        boolean sameLocation = f_bsLocation.getText().equals(m_settings.getLocation());
        boolean sameWorkingDir = f_workingDir.getText().equals(m_settings.getWorkingDir());
        boolean sameRefmtOnSave = f_reformatOnSave.isSelected() == m_settings.isRefmtOnSave();
        boolean sameColWidth = f_columnWidth.getText().equals(m_settings.getRefmtWidth());
        return !(sameEnabled && sameLocation && sameWorkingDir && sameRefmtOnSave && sameColWidth);
    }

    @Override
    public void reset() {
        f_enabled.setSelected(m_settings.isEnabled());
        f_bsLocation.setText(m_settings.getLocation());
        f_workingDir.setText(m_settings.getWorkingDir());
        f_columnWidth.setText(m_settings.getRefmtWidth());
        f_reformatOnSave.setSelected(m_settings.isRefmtOnSave());
    }

}
