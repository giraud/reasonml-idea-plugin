package com.reason.ide.facet;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;

class DuneFacetEditor extends FacetEditorTab {

    private final FacetEditorContext m_editorContext;
    private final DuneFacetConfiguration m_configuration;

    private JPanel f_root;
    private JCheckBox f_esyCheck;

    DuneFacetEditor(FacetEditorContext editorContext, DuneFacetConfiguration configuration) {
        m_editorContext = editorContext;
        m_configuration = configuration;
    }

    @NotNull
    @Nls
    @Override
    public String getDisplayName() {
        return "Dune/Esy";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        DuneFacetConfiguration state = m_configuration.getState();
        assert state != null;

        f_esyCheck.setSelected(state.isEsy);
        return f_root;
    }

    @Override
    public boolean isModified() {
        return m_configuration.isEsy != f_esyCheck.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        m_configuration.isEsy = f_esyCheck.isSelected();
    }
}
