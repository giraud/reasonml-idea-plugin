package com.reason.ide.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OCamlFacetEditor extends FacetEditorTab {

    private final FacetEditorContext m_editorContext;
    private final OCamlFacetSettings m_configuration;

    private JPanel m_rootPanel;
    private JTextField m_locationField;
    private JCheckBox m_refmtOnSaveField;
    private JTextField m_refmtWidthField;

    public OCamlFacetEditor(@SuppressWarnings("UnusedParameters") FacetEditorContext editorContext, OCamlFacetSettings configuration) {
        m_editorContext = editorContext;
        m_configuration = configuration;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "OCaml";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        OCamlFacetSettings state = m_configuration.getState();
        if (state != null) {
            m_locationField.setText(state.location);
            m_refmtOnSaveField.setSelected(state.refmtOnSave);
            m_refmtWidthField.setText(state.refmtWidth);
        }
        return m_rootPanel;
    }

    @Override
    public boolean isModified() {
        return !(m_locationField.getText().equals(m_configuration.location) &&
                m_refmtOnSaveField.isSelected() == m_configuration.refmtOnSave &&
                m_refmtWidthField.getText().equals(m_configuration.refmtWidth));
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        m_configuration.location = m_locationField.getText();
        m_configuration.refmtOnSave = m_refmtOnSaveField.isSelected();
        m_configuration.refmtWidth = m_refmtWidthField.getText();
    }
}
