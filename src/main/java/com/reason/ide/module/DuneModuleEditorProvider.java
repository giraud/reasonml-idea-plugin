package com.reason.ide.module;

import com.intellij.openapi.module.*;
import com.intellij.openapi.roots.ui.configuration.*;

public class DuneModuleEditorProvider implements ModuleConfigurationEditorProviderEx {
    public DuneModuleEditorProvider() {
    }

    public boolean isCompleteEditorSet() {
        return false;
    }

    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        //Module module = state.getCurrentRootModel().getModule();
        return new ModuleConfigurationEditor[]{new DuneModuleEditor(state)};
    }
}
