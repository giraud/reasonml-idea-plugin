package com.reason.ide.facet;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;

public class BsFacetEditor extends FacetEditorTab {

    private final BsFacetSettings m_settings;

    private JPanel f_rootPanel;
    private JTextField f_bsLocation;
    private JTextField f_columnWidth;
    private JCheckBox f_reformatOnSave;
    private JButton f_selectLocation;

    public BsFacetEditor(FacetEditorContext editorContext, BsFacetSettings settings) {
        m_settings = settings;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Bucklescript";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        BsFacetSettings state = m_settings.getState();
        if (state != null) {
            f_bsLocation.setText(state.location);
            f_reformatOnSave.setSelected(state.refmtOnSave);
            f_columnWidth.setText(state.refmtWidth);
        }
        return f_rootPanel;
    }

    @Override
    public boolean isModified() {
        return !(f_bsLocation.getText().equals(m_settings.location) &&
                f_reformatOnSave.isSelected() == m_settings.refmtOnSave &&
                f_columnWidth.getText().equals(m_settings.refmtWidth));
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
    }
}
