package com.reason.ide.facet;

import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;

public class BsFacetEditor extends FacetEditorTab {

    private final FacetEditorContext m_editorContext;
    private final BsFacetSettings m_settings;

    private JPanel f_rootPanel;
    private com.intellij.openapi.ui.TextFieldWithBrowseButton f_bsLocation;
    private JTextField f_columnWidth;
    private JCheckBox f_reformatOnSave;

    BsFacetEditor(FacetEditorContext editorContext, BsFacetSettings settings) {
        m_editorContext = editorContext;
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
        boolean sameLocation = f_bsLocation.getText().equals(m_settings.location);
        boolean sameRfmtOnSave = f_reformatOnSave.isSelected() == m_settings.refmtOnSave;
        boolean sameColWidth = f_columnWidth.getText().equals(m_settings.refmtWidth);
        return !(sameLocation && sameRfmtOnSave && sameColWidth);
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        m_settings.location = f_bsLocation.getText();
        m_settings.refmtOnSave = f_reformatOnSave.isSelected();
        m_settings.refmtWidth = f_columnWidth.getText();
    }
}
