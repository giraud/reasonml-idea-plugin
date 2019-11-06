package com.reason.module;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.roots.ui.configuration.ContentRootPanel;
import com.intellij.openapi.roots.ui.configuration.ModuleSourceRootEditHandler;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.roots.IconActionComponent;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class OCamlBinaryRootEditHandler extends ModuleSourceRootEditHandler<OCamlBinaryRootProperties> {
    public OCamlBinaryRootEditHandler() {
        super(OCamlBinaryRootType.BINARY);
    }


    @Nullable
    @Override
    public Icon getFolderUnderRootIcon() {
        return null;
    }

    @Nullable
    @Override
    public CustomShortcutSet getMarkRootShortcutSet() {
        return null;
    }

    @Nullable
    @Override
    public String getPropertiesString(@NotNull OCamlBinaryRootProperties properties) {
        StringBuilder buffer = new StringBuilder();
        if (properties.isForGeneratedSources()) {
            buffer.append(" [generated]");
        }
        String relativeOutputPath = properties.getRelativeOutputPath();
        if (!relativeOutputPath.isEmpty()) {
            buffer.append(" (").append(relativeOutputPath).append(")");
        }
        return buffer.length() > 0 ? buffer.toString() : null;
    }

    @Nullable
    @Override
    public JComponent createPropertiesEditor(@NotNull final SourceFolder folder,
                                             @NotNull final JComponent parentComponent,
                                             @NotNull final ContentRootPanel.ActionCallback callback) {
        final IconActionComponent iconComponent = new IconActionComponent(AllIcons.General.Inline_edit,
                AllIcons.General.Inline_edit_hovered,
                ProjectBundle.message("module.paths.edit.properties.tooltip"),
                () -> {
                    OCamlBinaryRootProperties properties = folder.getJpsElement().getProperties(OCamlBinaryRootType.BINARY);
                    assert properties != null;
                    OCamlBinaryRootEditHandler.ResourceRootPropertiesDialog
                            dialog = new OCamlBinaryRootEditHandler.ResourceRootPropertiesDialog(parentComponent, properties);
                    if (dialog.showAndGet()) {
                        callback.onSourceRootPropertiesChanged(folder);
                    }
                });
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(iconComponent, BorderLayout.CENTER);
        panel.add(Box.createHorizontalStrut(3), BorderLayout.EAST);
        return panel;
    }

    private static class ResourceRootPropertiesDialog extends DialogWrapper {
        @NotNull
        private final JTextField myRelativeOutputPathField;
        @NotNull
        private final JCheckBox myIsGeneratedCheckBox;
        private final JPanel myMainPanel;
        @NotNull
        private final OCamlBinaryRootProperties myProperties;

        private ResourceRootPropertiesDialog(@NotNull JComponent parentComponent, @NotNull OCamlBinaryRootProperties properties) {
            super(parentComponent, true);
            myProperties = properties;
            setTitle(ProjectBundle.message("module.paths.edit.properties.title"));
            myRelativeOutputPathField = new JTextField();
            myIsGeneratedCheckBox = new JCheckBox(UIUtil.replaceMnemonicAmpersand("For &generated resources"));
            myMainPanel = FormBuilder.createFormBuilder()
                    .addLabeledComponent("Relative output &path:", myRelativeOutputPathField)
                    .addComponent(myIsGeneratedCheckBox)
                    .getPanel();
            myRelativeOutputPathField.setText(myProperties.getRelativeOutputPath());
            myRelativeOutputPathField.setColumns(25);
            myIsGeneratedCheckBox.setSelected(myProperties.isForGeneratedSources());
            init();
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return myRelativeOutputPathField;
        }

        @Override
        protected void doOKAction() {
            myProperties.setRelativeOutputPath(normalizePath(myRelativeOutputPathField.getText()));
            myProperties.setForGeneratedSources(myIsGeneratedCheckBox.isSelected());
            super.doOKAction();
        }

        @NotNull
        private static String normalizePath(@NotNull String path) {
            return StringUtil.trimEnd(StringUtil.trimStart(FileUtil.toSystemIndependentName(path.trim()), "/"), "/");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return myMainPanel;
        }
    }

    @NotNull
    @Override
    public String getRootTypeName() {
        return "Binary";
    }

    @NotNull
    @Override
    public Icon getRootIcon() {
        return AllIcons.Modules.GeneratedSourceRoot;
    }

    @NotNull
    @Override
    public String getRootsGroupTitle() {
        return "Binary Folders";
    }

    @NotNull
    @Override
    public Color getRootsGroupColor() {
        return new JBColor(new Color(0x812DF3), new Color(127, 96, 144));
    }

    @NotNull
    @Override
    public String getUnmarkRootButtonText() {
        return "Unmark Binary";
    }
}
