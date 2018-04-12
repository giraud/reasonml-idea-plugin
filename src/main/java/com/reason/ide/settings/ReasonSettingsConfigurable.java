package com.reason.ide.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private final ReasonSettings m_settings;
    private ReasonConfigurablePanel m_panel;

    public ReasonSettingsConfigurable(ReasonSettings settings) {
        m_settings = settings;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

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
        m_panel = new ReasonConfigurablePanel();
        return m_panel.myWholePanel;
    }

    @Override
    public boolean isModified() {
        return m_panel.isModified(m_settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        m_panel.apply(m_settings);
    }

    @Override
    public void reset() {
        m_panel.reset(m_settings);
    }

    @Override
    public void disposeUIResources() {
        m_panel = null;
    }

    public static class ReasonConfigurablePanel {
        private JTextField myRefmtWidth;
        private JPanel myWholePanel;

        private void reset(ReasonSettings settings) {
            final String pathToSceneBuilder = settings.getRefmtWidth();
            myRefmtWidth.setText(pathToSceneBuilder);
        }

        private void apply(ReasonSettings settings) {
            settings.setRefmtWidth(myRefmtWidth.getText().trim());
        }

        private boolean isModified(ReasonSettings settings) {
            final String refmtWidth = settings.getRefmtWidth();
            return !Comparing.strEqual(myRefmtWidth.getText().trim(), refmtWidth.trim());
        }
    }
}
