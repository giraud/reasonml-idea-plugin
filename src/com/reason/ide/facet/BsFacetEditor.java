package com.reason.ide.facet;

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BsFacetEditor extends FacetEditorTab {

    private final FacetEditorContext m_editorContext;
    private final BsFacetConfiguration m_configuration;

    private JPanel f_rootPanel;
    private com.intellij.openapi.ui.TextFieldWithBrowseButton f_bsLocation;
    private JTextField f_columnWidth;
    private JCheckBox f_reformatOnSave;

    BsFacetEditor(FacetEditorContext editorContext, BsFacetConfiguration configuration) {
        m_editorContext = editorContext;
        m_configuration = configuration;
    }

    @NotNull
    @Nls
    @Override
    public String getDisplayName() {
        return "Bucklescript";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        BsFacetConfiguration state = m_configuration.getState();
        assert state != null;

        f_bsLocation.setText(state.location);
        f_bsLocation.addBrowseFolderListener("Choose bs-platform directory: ", null, m_editorContext.getProject(),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        f_reformatOnSave.setSelected(state.refmtOnSave);
        f_columnWidth.setText(state.refmtWidth);


        return f_rootPanel;
    }

    @Override
    public boolean isModified() {
        boolean sameLocation = f_bsLocation.getText().equals(m_configuration.location);
        boolean sameRfmtOnSave = f_reformatOnSave.isSelected() == m_configuration.refmtOnSave;
        boolean sameColWidth = f_columnWidth.getText().equals(m_configuration.refmtWidth);
        return !(sameLocation && sameRfmtOnSave && sameColWidth);
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        m_configuration.location = f_bsLocation.getText().trim();
        m_configuration.refmtOnSave = f_reformatOnSave.isSelected();
        m_configuration.refmtWidth = f_columnWidth.getText().trim();
    }
}
