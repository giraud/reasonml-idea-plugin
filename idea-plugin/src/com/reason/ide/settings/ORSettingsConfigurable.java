package com.reason.ide.settings;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

public class ORSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    @Nls
    private static final String OCAMLFORMAT_EXECUTABLE_LABEL = "Choose ocamlformat Executable: ";

    @Nls
    private static final String BS_PLATFORM_LOCATION_LABEL = "Choose bs-platform Directory: ";

    @Nls
    private static final String DUNE_EXECTUABLE_LABEL = "Choose dune Executable: ";

    @Nls
    private static final String ESY_EXECTUABLE_LABEL = "Choose esy Executable: ";

    private final ORSettings m_settings;

    private JPanel f_rootPanel;
    private JTabbedPane f_tabs;

    // General
    private JTextField f_generalFormatWidthColumns;
    private JCheckBox f_generalIsFormatOnSave;
    private TextFieldWithBrowseButton f_generalOcamlformatExecutable;

    // BuckleScript
    private JCheckBox f_bsIsEnabled;
    private TextFieldWithBrowseButton f_bsPlatformLocation;

    // Dune
    private TextFieldWithBrowseButton f_duneExecutable;

    // Esy
    private TextFieldWithBrowseButton f_esyExecutable;

    public ORSettingsConfigurable(ORSettings settings) {
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
        return "OCaml / Reason";
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "settings.reason";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        createGeneralTab();
        createBsTab();
        createDuneTab();
        createEsyTab();
        return f_rootPanel;
    }

    @Override
    public void apply() {
        // General
        m_settings.setFormatOnSaveEnabled(f_generalIsFormatOnSave.isSelected());
        m_settings.setFormatColumnWidth(sanitizeInput(f_generalFormatWidthColumns));
        m_settings.setOcamlformatExecutable(sanitizeInput(f_generalOcamlformatExecutable));
        // BuckleScript
        m_settings.setBsEnabled(f_bsIsEnabled.isSelected());
        m_settings.setBsPlatformLocation(sanitizeInput(f_bsPlatformLocation));
        // Dune
        m_settings.setDuneExecutable(sanitizeInput(f_duneExecutable));
        // Esy
        m_settings.setEsyExecutable(sanitizeInput(f_esyExecutable));
    }

    @Override
    public boolean isModified() {
        boolean isFormatOnSaveModified = f_generalIsFormatOnSave.isSelected() != m_settings.isFormatOnSaveEnabled();
        boolean isFormatWidthColumnsModified = !f_generalFormatWidthColumns.getText().equals(m_settings.getFormatColumnWidth());
        boolean isOcamlFormatExecutableModified = !f_generalOcamlformatExecutable.getText().equals(m_settings.getOcamlformatExecutable());
        boolean isBsEnabledModified = f_bsIsEnabled.isSelected() != m_settings.isBsEnabled();
        boolean isBsPlatformLocationModified = !f_bsPlatformLocation.getText().equals(m_settings.getBsPlatformLocation());
        boolean isDuneExecutableModified = !f_duneExecutable.getText().equals(m_settings.getDuneExecutable());
        boolean isEsyExecutableModified = !f_esyExecutable.getText().equals(m_settings.getEsyExecutable());
        return isFormatOnSaveModified || isFormatWidthColumnsModified || isOcamlFormatExecutableModified || isBsEnabledModified || isBsPlatformLocationModified
                || isDuneExecutableModified || isEsyExecutableModified;
    }

    @Override
    public void reset() {
        // General
        f_generalIsFormatOnSave.setSelected(m_settings.isFormatOnSaveEnabled());
        f_generalFormatWidthColumns.setText(m_settings.getFormatColumnWidth());
        f_generalOcamlformatExecutable.setText(m_settings.getOcamlformatExecutable());
        // BuckleScript
        f_bsIsEnabled.setSelected(m_settings.isBsEnabled());
        f_bsPlatformLocation.setText(m_settings.getBsPlatformLocation());
        // Dune
        f_duneExecutable.setText(m_settings.getDuneExecutable());
        // Esy
        f_esyExecutable.setText(m_settings.getEsyExecutable());
    }

    private void createGeneralTab() {
        Project project = m_settings.getProject();
        f_generalOcamlformatExecutable
                .addBrowseFolderListener(OCAMLFORMAT_EXECUTABLE_LABEL, null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    private void createBsTab() {
        Project project = m_settings.getProject();
        f_bsPlatformLocation.addBrowseFolderListener(BS_PLATFORM_LOCATION_LABEL, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }

    private void createDuneTab() {
        Project project = m_settings.getProject();
        f_duneExecutable
                .addBrowseFolderListener(DUNE_EXECTUABLE_LABEL, null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    private void createEsyTab() {
        Project project = m_settings.getProject();
        f_esyExecutable.addBrowseFolderListener(ESY_EXECTUABLE_LABEL, null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    private static String sanitizeInput(JTextField textField) {
        return sanitizeInput(textField.getText());
    }

    private static String sanitizeInput(TextFieldWithBrowseButton textFieldWithBrowseButton) {
        return sanitizeInput(textFieldWithBrowseButton.getText());
    }

    private static String sanitizeInput(String input) {
        return input.trim();
    }
}
