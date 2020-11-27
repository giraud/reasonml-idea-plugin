package com.reason.ide.settings;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ORSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {

  @Nls
  private static final String BS_PLATFORM_LOCATION_LABEL = "Choose bs-platform Directory: ";
  @Nls
  private static final String ESY_EXECUTABLE_LABEL = "Choose esy Executable: ";

  private final @NotNull Project m_project;
  private ORSettings m_settings;

  private JPanel f_rootPanel;
  private JTabbedPane f_tabs;

  // General
  private JTextField f_generalFormatWidthColumns;
  private JCheckBox f_generalIsFormatOnSave;

  // BuckleScript
  private JCheckBox f_bsIsEnabled;
  private TextFieldWithBrowseButton f_bsPlatformLocation;

  // Esy
  private TextFieldWithBrowseButton f_esyExecutable;

  public ORSettingsConfigurable(@NotNull Project project) {
    m_project = project;
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
    m_settings = ORSettings.getInstance(m_project);
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
    // BuckleScript
    m_settings.setBsEnabled(f_bsIsEnabled.isSelected());
    m_settings.setBsPlatformLocation(sanitizeInput(f_bsPlatformLocation));
    // Esy
    m_settings.setEsyExecutable(sanitizeInput(f_esyExecutable));
  }

  @Override
  public boolean isModified() {
    boolean isFormatOnSaveModified =
        f_generalIsFormatOnSave.isSelected() != m_settings.isFormatOnSaveEnabled();
    boolean isFormatWidthColumnsModified =
        !f_generalFormatWidthColumns.getText().equals(m_settings.getFormatColumnWidth());
    boolean isBsEnabledModified = f_bsIsEnabled.isSelected() != m_settings.isBsEnabled();
    boolean isBsPlatformLocationModified =
        !f_bsPlatformLocation.getText().equals(m_settings.getBsPlatformLocation());
    boolean isEsyExecutableModified =
        !f_esyExecutable.getText().equals(m_settings.getEsyExecutable());
    return isFormatOnSaveModified
               || isFormatWidthColumnsModified
               || isBsEnabledModified
               || isBsPlatformLocationModified
               || isEsyExecutableModified;
  }

  @Override
  public void reset() {
    // General
    f_generalIsFormatOnSave.setSelected(m_settings.isFormatOnSaveEnabled());
    f_generalFormatWidthColumns.setText(m_settings.getFormatColumnWidth());
    // BuckleScript
    f_bsIsEnabled.setSelected(m_settings.isBsEnabled());
    f_bsPlatformLocation.setText(m_settings.getBsPlatformLocation());
    // Esy
    f_esyExecutable.setText(m_settings.getEsyExecutable());
  }

  private void createGeneralTab() {
  }

  private void createBsTab() {
    Project project = m_settings.getProject();
    f_bsPlatformLocation.addBrowseFolderListener(
        BS_PLATFORM_LOCATION_LABEL,
        null,
        project,
        FileChooserDescriptorFactory.createSingleFolderDescriptor());
  }

  private void createDuneTab() {
  }

  private void createEsyTab() {
    Project project = m_settings.getProject();
    f_esyExecutable.addBrowseFolderListener(
        ESY_EXECUTABLE_LABEL,
        null,
        project,
        FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
  }

  private static @NotNull String sanitizeInput(@NotNull JTextField textField) {
    return sanitizeInput(textField.getText());
  }

  private static @NotNull String sanitizeInput(
      @NotNull TextFieldWithBrowseButton textFieldWithBrowseButton) {
    return sanitizeInput(textFieldWithBrowseButton.getText());
  }

  private static @NotNull String sanitizeInput(@NotNull String input) {
    return input.trim();
  }
}
