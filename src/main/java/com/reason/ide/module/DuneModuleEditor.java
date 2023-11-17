package com.reason.ide.module;

import com.intellij.openapi.roots.ui.configuration.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class DuneModuleEditor extends ModuleElementsEditor {
    private JPanel myPanel;

    protected DuneModuleEditor(@NotNull ModuleConfigurationState state) {
        super(state);
    }

    @Override
    protected JComponent createComponentImpl() {
        return myPanel;
    }

    @NonNls
    @Override
    public String getDisplayName() {
        return "Dune";
    }
}
