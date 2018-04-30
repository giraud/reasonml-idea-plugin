package com.reason.ide.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

public class OCamlFacetEditor extends FacetEditorTab {

    private final OCamlFacetConfiguration m_configuration;

    private JPanel m_rootPanel;
    private JTextField m_locationField;
    private boolean m_isModified;

    public OCamlFacetEditor(@SuppressWarnings("UnusedParameters") FacetEditorContext editorContext, OCamlFacetConfiguration configuration) {
        m_configuration = configuration;
        m_locationField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                m_isModified = true;
            }
        });
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "OCaml";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        m_locationField.setText(m_configuration.getState().location);
        return m_rootPanel;
    }

    @Override
    public boolean isModified() {
        return m_isModified;
    }
}
